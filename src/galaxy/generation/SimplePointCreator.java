/**
 * 
 */
package galaxy.generation;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.geom.Vector2f;

/**
 * @author Daniel Langdon
 */
public class SimplePointCreator implements ForceOfNature
{
	private int exclusionRadius;
	private int maxFailures;
	private float explosionFactor;
	
	/**
	 * Creates a bunch of points.
	 * @param exclusionRadius
	 * @param maxFailures
	 * @param explosionFactor The random coordinates created relate to the heatmap size, so this multiplier is used to get intended world coordinates. 
	 */
	public SimplePointCreator(int exclusionRadius, int maxFailures, float explosionFactor)
	{
		this.exclusionRadius = exclusionRadius;
		this.maxFailures = maxFailures;
		this.explosionFactor = explosionFactor;
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#apply(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean apply(NascentGalaxy nascent)
	{
		if(nascent.heatmap == null)
			return false;
		
		Random rand = new Random();
		float[][] map = nascent.heatmap;
		nascent.points = new ArrayList<Vector2f>();
		
		int failures = 0;
		while(failures <= maxFailures)
		{
			// Find a new point to place with probability given by the inverse heatmap.
			int x = rand.nextInt(map.length);
			int y = rand.nextInt(map[0].length);
			float prob = rand.nextFloat();
			if(1.0f - map[x][y] < prob)
			{
				failures++;
				continue;
			}
			nascent.points.add(new Vector2f(x*explosionFactor, y*explosionFactor));
			
			// Paint an exclusion radius to avoid points being too close to each other.
			for(int i=-exclusionRadius; i<=exclusionRadius; i++)
				for(int j=-exclusionRadius; j<=exclusionRadius; j++)
					if(x+i >= 0 && x+i < map.length && y+j >= 0 && y+j < map[0].length && (i*i + j*j < exclusionRadius*exclusionRadius))
						map[x+i][y+j] = 1.0f;
		}
		System.out.println("INFO: Created " + nascent.points.size() + " points.");
		return true;
	}

}
