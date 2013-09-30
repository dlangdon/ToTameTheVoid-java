/**
 * 
 */
package galaxy.generation;

import galaxy.generation.NascentGalaxy.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import org.newdawn.slick.geom.Vector2f;

/**
 * Stage that tries to find the correct location to place the initial empires.
 * It uses a modified K-medoids algorithm that does gradient descent on the number of stars closer to each starting location.
 * @author Daniel Langdon
 */
public class EmpirePlacer implements ForceOfNature
{
	class Node implements Comparable<Node>
	{
		int id;
		Medoid medoid;
		double distance;
		int lastModified;
		ArrayList<Integer> outwardConnections;
		ArrayList<Double> outwardDistances;
		
		@Override
		public int compareTo(Node other)
		{
			return Double.compare(this.distance, other.distance);
		}
	}
	
	class Medoid implements Comparable<Medoid>
	{
		int clusterSize;
		int location;

		@Override
		public int compareTo(Medoid other)
		{
			return clusterSize - other.clusterSize;
		}
	}

	int numEmpires;
	List<Medoid> medoids;
	List<Node> nodes;
	HashMap<Integer, Node> revertInfo;
	NascentGalaxy nascent;
	int updateCount;
	
	/**
	 * 
	 */
	public EmpirePlacer(int numEmpires)
	{
		this.numEmpires = numEmpires;
		this.updateCount = 0;
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#unleash(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascentGalaxy)
	{
		try
		{
			init(nascentGalaxy);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}

		while(step()){}
		updateGalaxy();
		return true;
	}
	
	/**
	 * Initializes all nodes and finds initial locations for all empires. Those locations are assigned to the relevant clusters, but others are left unallocated (null). 
	 */
	void init(NascentGalaxy nascent) throws Exception
	{
		this.nascent = nascent;

		// Initialize the mappings (this is only an optimization for later). 
		// Initially, all points belong to the first medoid.
		nodes = new ArrayList<Node>();
		for(int i=0; i<nascent.points.size(); i++)
		{
			Node newNode = new Node();
			newNode.id = i;
			newNode.distance = Double.MAX_VALUE;
			newNode.medoid = null;
			newNode.outwardConnections = new ArrayList<Integer>();
			newNode.outwardDistances = new ArrayList<Double>();
			for(int j=0; j<nascent.points.size(); j++)
			{
				Edge edge = new Edge(i, j);
				if(nascent.prunedEdges.contains(edge))
				{
					Vector2f p1 = nascent.points.get(i);
					Vector2f p2 = nascent.points.get(j);
					double distance = (p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y);
					newNode.outwardConnections.add(j);
					newNode.outwardDistances.add(distance);
				}
			}
			nodes.add(newNode);
		}
		
		// Place 5 centers randomly
		Random rand = new Random();
		medoids = new ArrayList<Medoid>();
		for(int i=0; i<numEmpires; i++)
		{
			int location = 0;
			for(int trials = 0; trials < 100; trials++)
			{
				location = rand.nextInt(nascent.points.size());
				if(validMedoidDistance(location, null))
					break;
				location = -1;
			}
			
			if(location < 0)
				throw new Exception("Could not find a reasonable starting point for empires!");

			Medoid toAdd = new Medoid();
			toAdd.location = location;
			toAdd.clusterSize = 1;
			medoids.add(toAdd);
		}
		
		// Now update all clusters
		updateBoundaries();
	}
	
	boolean step()
	{
		// Resort medoids by size, so the search tries always to enlarge the smallest cluster first.
		List<Medoid> sortedMedoids = new ArrayList<EmpirePlacer.Medoid>(medoids);
		Collections.sort(sortedMedoids);
		
		// Score for this configuration is just the size distance between the biggest cluster and the smallest.
		int score = sortedMedoids.get(sortedMedoids.size()-1).clusterSize - sortedMedoids.get(0).clusterSize;
		
		for(Medoid m : sortedMedoids)
		{
			for(Integer option : nodes.get(m.location).outwardConnections)
			{
				// Check if the jump would put us too close.
				if(!validMedoidDistance(option, m))
					continue;
				
				// Do the jump, update the distribution of nodes and see what happens.
				System.out.format("Moving cluster %d --> %d", m.location, option);
				int oldLocation = m.location;
				m.location = option;
				updateBoundaries();
				System.out.format("done.\n");

				List<Medoid> newSortedMedoids = new ArrayList<EmpirePlacer.Medoid>(medoids);
				Collections.sort(newSortedMedoids);
				int newScore = newSortedMedoids.get(newSortedMedoids.size()-1).clusterSize - newSortedMedoids.get(0).clusterSize;
				
				if(newScore >= score)	// Can't be greater only or it might fall into an infinite loop.
				{
					// Revert and keep trying
					System.out.format("Score %d >= %d, reverting", newScore, score);
					m.location = oldLocation;
					updateBoundaries();
					System.out.format("done.\n");
				}
				else
					return true;
			}
		}
		return false;
	}
	
	void updateBoundaries()
	{
		updateCount++;
		PriorityQueue<Node> toExpand = new PriorityQueue<Node>();
		for(Medoid m : medoids)
		{
			Node n = nodes.get(m.location);
			n.distance = 0.0;
			n.medoid = m;
			n.lastModified = updateCount;
			toExpand.add(n);
		}

		while(!toExpand.isEmpty())
		{
			// Same node can be enqueue more than once, if reached from many nodes. Not a problem since operations are harmless.
			Node next = toExpand.remove();	 
			
			for (int i = 0 ; i<next.outwardConnections.size(); i++) 
			{
				Node destination = nodes.get(next.outwardConnections.get(i));
				if(destination.lastModified < updateCount || next.outwardDistances.get(i) + next.distance < destination.distance)
				{
//					System.out.format("Reasigning node %d from cluster %d (%f) to cluster %d (%f)\n", nodes.indexOf(destination), medoids.indexOf(destination.medoid), destination.distance, medoids.indexOf(next.medoid), entry.getKey() + next.distance);
					if(destination.medoid != null)
						destination.medoid.clusterSize--;
					next.medoid.clusterSize++;
					destination.medoid = next.medoid;
					destination.lastModified = updateCount;
					destination.distance = next.outwardDistances.get(i) + next.distance; 
					toExpand.add(destination);
				}
			}
		}
	}

	void updateGalaxy()
	{
		nascent.startingLocations = new ArrayList<Integer>();
		for(Medoid m : medoids)
			nascent.startingLocations.add(m.location);
	}

	boolean validMedoidDistance(int location, Medoid skip)
	{
		for(Medoid m: medoids)
		{
			Edge edge = new Edge(m.location, location);
			if(m != skip && (m.location == location || nascent.prunedEdges.contains(edge)))
				return false;
		}
		return true;
	}
	
}
