/**
 *
 */
package com.github.dlangdon.galaxy.generation;

import java.util.ArrayList;

import org.newdawn.slick.geom.Vector2f;

import com.github.dlangdon.state.Star;

/**
 * A simple class that creates stars with random parameters
 * @author Daniel Langdon
 */
public class RandomStarGenerator implements ForceOfNature
{
	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.generation.ForceOfNature#unleash(com.github.dlangdon.com.github.dlangdon.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascentGalaxy)
	{
		if(nascentGalaxy.points == null)
			return false;
		nascentGalaxy.bornStars = new ArrayList<Star>();

		int i=0;
		for(Vector2f p : nascentGalaxy.points)
		{
			Star s = new Star(i, p.x, p.y);
			s.setName("Star " + i);
			s.setParameters((float)Math.random(), (float)Math.random(), (float)Math.random());
			nascentGalaxy.bornStars.add(s);
			i++;
		}
		return true;
	}

}
