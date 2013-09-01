/**
 * 
 */
package galaxy.generation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.newdawn.slick.geom.Vector2f;

import state.Star;

/**
 * This is a common data structure to support the pipeline that produces a full galaxy map.
 * Ideally, one or more processes are registered to work on top of this data structure, adding or subtracting from it.
 * Most of the data is package-accessible, and null if no valid data has been created yet.
 * @author Daniel Langdon
 */
public class NascentGalaxy
{
	/**
	 * An edge between two points, stored from their index in the point list.
	 * The first index, v1, is always less or equal than the second one.
	 */
	class Edge
	{
		public int v1;
		public int v2;
		
		/**
		 * @param v12
		 * @param v22
		 */
		public Edge(int v1, int v2)
		{
			this.v1 = v1 < v2 ? v1 : v2;
			this.v2 = v1 < v2 ? v2 : v1;
		}

		@Override
		public boolean equals(Object other)
		{
			if(other == null || !(other instanceof Edge))
				return false;
			
			Edge o = (Edge)other;
			if((v1 == o.v1 && v2 == o.v2) || (v1 == o.v2 && v2 == o.v1)) 
				return true;
			return false;
		}
		
		@Override
		public int hashCode()
		{
			return v1 < v2 ? v1 * points.size() + v2 : v2 * points.size() + v1;
		}
		
		@Override
		public String toString()
		{
			return String.format("%d-%d", v1, v2);
		}

	}
	
	float[][] heatmap;
	List<Vector2f> points;
	Set<Edge> initialLanes;
	Set<Edge> prunedLanes;
	List<Star> bornStars;
	List<ForceOfNature> forces;
	
	/**
	 * 
	 */
	public NascentGalaxy()
	{
		heatmap = null;
		points = null;
		initialLanes = null;
		prunedLanes = null;
		bornStars = null;
		forces = new LinkedList<ForceOfNature>();
	}
	
	/**
	 * @param force Force of nature to be added to shape this newborn galaxy. The order in which these forces are added is extremely important.
	 */
	public void addForce(ForceOfNature force)
	{
		forces.add(force);
	}
	
	/**
	 * Runs all preconfigured forces of nature in order, creating a new galaxy.
	 * @return true if the galaxy was successfully created (all forces were successful), else false.
	 */
	public boolean runAllForces()
	{
		for(ForceOfNature force : forces)
			if(!force.unleash(this))
				return false;
		return true;
	}
}
