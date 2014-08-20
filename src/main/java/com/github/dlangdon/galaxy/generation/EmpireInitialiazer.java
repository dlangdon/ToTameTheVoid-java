/**
 *
 */
package com.github.dlangdon.galaxy.generation;

import com.github.dlangdon.military.Shipyard;

import org.newdawn.slick.Color;

import com.github.dlangdon.state.Colony;
import com.github.dlangdon.empire.Empire;
import com.github.dlangdon.state.Fleet;
import com.github.dlangdon.state.Star;
import com.github.dlangdon.state.Unit;

/**
 * Takes starting locations and creates actual empires, as well as their starting configurations.
 * All empires start on a completely average star, with two scouts and two colony ships in orbit.
 * @author Daniel Langdon
 */
public class EmpireInitialiazer implements ForceOfNature
{
	private static Color[] palette = new Color[]{
		new Color(210, 49, 93),
		new Color(247, 200, 8),
		new Color(34, 181, 191),
		new Color(135, 103, 166),
		new Color(233, 136, 19),
		new Color(136, 193, 52)
	};

	private static String[] names = new String[]{
		"Human Confederation",
		"Blor Hive",
		"Krith Matrix",
		"Outcasts",
		"Udoids",
		"Tash-rak"
	};

	int numEmpires;

	public EmpireInitialiazer(int numEmpires)
	{
		this.numEmpires = numEmpires;
	}

	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.generation.ForceOfNature#unleash(com.github.dlangdon.com.github.dlangdon.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascentGalaxy)
	{
		if(numEmpires >= names.length)
			return false;

		for(int i=0; i<numEmpires; i++)
		{
			Empire e = new Empire(names[i], palette[i]);
			Star s = Star.all().get(nascentGalaxy.startingLocations.get(i));
			s.setParameters(0.5f, 0.5f, 0.5f);

			Fleet f = new Fleet(s, e);
			f.addUnits(Unit.fetchByName("Figther"), 2);
			f.addUnits(Unit.fetchByName("Colony Ship"), 1);

			new Colony(s, e);
			new Shipyard(s);
		}
		Empire.setPlayerEmpire(Empire.all().get(0));
		return true;
	}

}
