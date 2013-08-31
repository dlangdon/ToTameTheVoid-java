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
	class Lane
	{
		public int a;
		public int b;
	}
	
	float[][] heatmap;
	List<Vector2f> points;
	Set<Lane> initialLanes;
	Set<Lane> prunedLanes;
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
			if(!force.apply(this))
				return false;
		return true;
	}
}
