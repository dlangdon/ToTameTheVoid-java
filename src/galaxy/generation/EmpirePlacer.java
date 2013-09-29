/**
 * 
 */
package galaxy.generation;

import galaxy.generation.NascentGalaxy.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

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
		Medoid medoid;
		double distance;
		int lastModified;
		TreeMap<Double, Integer> outwardConnections;
		
		@Override
		public int compareTo(Node other)
		{
			if(this == other)
				return 0;
			return distance > other.distance ? 1 : -1;
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

		while(!step()){}
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
			newNode.distance = Double.MAX_VALUE;
			newNode.medoid = null;
			newNode.outwardConnections = new TreeMap<Double, Integer>();
			for(int j=0; j<nascent.points.size(); j++)
			{
				Edge edge = new Edge(i, j);
				if(nascent.prunedEdges.contains(edge))
				{
					Vector2f p1 = nascent.points.get(i);
					Vector2f p2 = nascent.points.get(j);
					double distance = (p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y);
					newNode.outwardConnections.put(distance, j);
				}
			}
			nodes.add(newNode);
		}
		
		// Place 5 centers randomly
		Random rand = new Random();
		medoids = new ArrayList<Medoid>();
		for(int i=0; i<numEmpires; i++)
		{
			int location = -1;
			for(int trials = 0; location< 0 && trials < 100; trials++)
			{
				location = rand.nextInt(nascent.points.size());

				// Validate no direct connection to previous medoids.
				for(Medoid m: medoids)
				{
					Edge edge = new Edge(m.location, location);
					if(m.location == location || nascent.prunedEdges.contains(edge))
					{
						location = -1;
						break;
					}
				}
			}
			
			if(location < 0)
				throw new Exception("Could not find a reasonable starting point for empires!");

			Medoid toAdd = new Medoid();
			toAdd.location = location;
			toAdd.clusterSize = 1;
			nodes.get(location).medoid = toAdd;
			medoids.add(toAdd);
		}
		
		// Now update all clusters
		for(Medoid m : medoids)
			UpdateBoundary(m);
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
			for(Integer option : nodes.get(m.location).outwardConnections.values())
			{
				// Check if the jump would put us too close.
				boolean tooClose = false;
				for(Medoid n : sortedMedoids)
				{
					Edge edge = new Edge(m.location, n.location);
					if(nascent.prunedEdges.contains(edge))
					{
						tooClose = true;
						break;
					}
				}
				if(tooClose)
					continue;
				
				// Do the jump, update the distribution of nodes and see what happens.
				System.out.format("Moving cluster %d --> %d\n", m.location, option);
				int oldLocation = m.location;
				m.location = option;
				UpdateBoundary(m);

				List<Medoid> newSortedMedoids = new ArrayList<EmpirePlacer.Medoid>(medoids);
				Collections.sort(newSortedMedoids);
				int newScore = newSortedMedoids.get(newSortedMedoids.size()-1).clusterSize - newSortedMedoids.get(0).clusterSize;
				
				if(newScore >= score)	// Can't be greater only or it might fall into an infinite loop.
				{
					// Revert and keep trying
					m.location = oldLocation;
					UpdateBoundary(m);
				}
				else
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Updates the boundary around a specific medoid. 
	 * This expands in a similar way to an MST, but only for those nodes marked as belonging to a cluster.
	 * If we are updating cluster A and a node is found belonging to cluster B, the distances are compared. Two thing can happen:
	 * - The node is switched from B to A and the update continues.
	 * - The update stops, and the node will then try to expand cluster B in reverse.
	 * So a run will first expand as much as possible, recalculating distances for the input cluster, then the boundary will contract.  
	 * @param m The medoid being updated.
	 */
	void UpdateBoundary(Medoid m)
	{
		TreeSet<Node> expansionQueue = new TreeSet<Node>();
		TreeSet<Node> contractionQueue = new TreeSet<Node>();

		// Phase 1, expansion.
		updateCount++;
		Node n = nodes.get(m.location);
		reassignCluster(n, m);
		n.distance = 0.0;
		expansionQueue.add(n);

		while(!expansionQueue.isEmpty())
		{
			Node next = expansionQueue.first();
			expansionQueue.remove(next);

			for (Entry<Double, Integer> entry : next.outwardConnections.entrySet()) 
			{
				Node destination = nodes.get(entry.getValue());
			
				// Case 0: if destination was already updated this round, stop right here.
				if(next.lastModified == updateCount)
					continue;
				
				// Case 1: same cluster node, needs distance update (can increase or decrease) and continue.
				if(destination.medoid == next.medoid)
				{
					destination.distance = next.distance + entry.getKey();
					destination.lastModified = updateCount;
					expansionQueue.add(destination);
				}
				
				// Case 2: different cluster.
				else
				{
					// 2.1 we can get there faster now, the node is ours.
					if(entry.getKey() + next.distance < destination.distance)
					{
						reassignCluster(destination, next.medoid);
						destination.lastModified = updateCount;
						destination.distance = entry.getKey() + next.distance; 
						expansionQueue.add(destination);
					}
					
					// 2.2 they can get here faster, the node is theirs. This works even if more this happens from more than one destination, as the shortest path wins.
					else if(entry.getKey() + destination.distance < next.distance )
					{
						reassignCluster(next, destination.medoid);
						next.distance = entry.getKey() + destination.distance;
						contractionQueue.add(next);
					}
					
					// 2.3 To each its own, no changes.
				}
			}
		}
		
		// Phase 2, contraction.
		updateCount++;
		while(!contractionQueue.isEmpty())
		{
			Node next = contractionQueue.first();
			contractionQueue.remove(next);

			for (Entry<Double, Integer> entry : next.outwardConnections.entrySet()) 
			{
				Node destination = nodes.get(entry.getValue());
			
				// If destination was not just updated on the recent expansion cycle, stop right here.
				if(next.lastModified != updateCount -1)
					continue;
				
				// Else we are guaranteed that the cluster is of a different color than us. See if we can keep growing.
				if(entry.getKey() + next.distance < destination.distance)
				{
					destination.medoid.clusterSize--;
					next.medoid.clusterSize++;
					destination.medoid = next.medoid;
					destination.lastModified = updateCount;
					destination.distance = entry.getKey() + next.distance; 
					contractionQueue.add(destination);
				}
			}
		}
	}
	
	void reassignCluster(Node n, Medoid m)
	{
		if(n.medoid != null)
			n.medoid.clusterSize--;
		m.clusterSize++;
		n.medoid = m;
	}

	void updateGalaxy()
	{
		nascent.startingLocations = new ArrayList<Integer>();
		for(Medoid m : medoids)
			nascent.startingLocations.add(m.location);
	}

}
