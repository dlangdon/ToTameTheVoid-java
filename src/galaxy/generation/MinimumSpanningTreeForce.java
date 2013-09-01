/**
 * 
 */
package galaxy.generation;

import galaxy.generation.NascentGalaxy.Edge;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.newdawn.slick.geom.Vector2f;

/**
 * A force that guarantees that no points are left unconnected. 
 * It builds a minimum spanning tree from the original edges and put it back into the list of prunned edges.
 * Prim et all algorithm is used for this.
 * @author Daniel Langdon
 */
public class MinimumSpanningTreeForce implements ForceOfNature
{
	static class QueuedEdge implements Comparable<QueuedEdge>
	{
		float distance;
		int v1;
		int v2;
		
		public QueuedEdge(float distance, int v1, int v2)
		{
			super();
			this.distance = distance;
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		public int compareTo(QueuedEdge other)
		{
			return distance > other.distance ? 1 : -1;
		}
	}
	
	NascentGalaxy nascent;
	HashSet<Edge> mst;

	private float squaredDistance(Edge edge)
	{
		if(!nascent.initialEdges.contains(edge))
			return -1.0f;
		
		Vector2f v1 = nascent.points.get(edge.v1);
		Vector2f v2 = nascent.points.get(edge.v2);
		
		return (v1.x - v2.x)*(v1.x - v2.x) + (v1.y - v2.y)*(v1.y - v2.y);
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#apply(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascent)
	{
		if(nascent.initialEdges == null || nascent.prunedEdges == null)
			return false;
		this.nascent = nascent;
		
		mst = new HashSet<Edge>();
		HashSet<Integer> visited = new HashSet<Integer>();
		TreeSet<QueuedEdge> frontier = new TreeSet<QueuedEdge>();
		int lastSelected = 0;

		while(visited.size() < nascent.points.size())
		{
			// Mark the point as visited
			visited.add(lastSelected);

			// Kill all edges coming from visited points to the new point (I'm already here)
			Iterator<QueuedEdge> i = frontier.iterator();
			while (i.hasNext())
			{
				QueuedEdge aux = i.next();
				if (aux.v2 == lastSelected || aux.v1 == lastSelected)
					i.remove();
			}

			// Add all edges from the new point to unvisited points.
			for(int j=0 ; j<nascent.points.size() ; j++)
			{
				if(!visited.contains(j))
				{
					Edge edge = new Edge(lastSelected, j);
					float distance = squaredDistance(edge);
					if(distance > 0)
						frontier.add(new QueuedEdge(distance, edge.v1, edge.v2));
				}
			}

			// So were to go next?
			if(!frontier.isEmpty())
			{
				QueuedEdge jump = frontier.first();
				lastSelected = visited.contains(jump.v1) ? jump.v2 : jump.v1;
				mst.add(new Edge(jump.v1, jump.v2));
			}
		}
		
		// Place all edges on the MST back into the prunned set.
		nascent.prunedEdges.addAll(mst);
		return true;
	}
}
