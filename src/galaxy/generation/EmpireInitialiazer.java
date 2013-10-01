/**
 * 
 */
package galaxy.generation;

import military.Shipyard;

import org.newdawn.slick.Color;

import state.Colony;
import state.Empire;
import state.Fleet;
import state.Star;
import state.Unit;

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
	 * @see galaxy.generation.ForceOfNature#unleash(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean unleash(NascentGalaxy nascentGalaxy)
	{
		if(numEmpires >= names.length)
			return false;
		
		Galaxy g = Galaxy.instance();
		for(int i=0; i<numEmpires; i++)
		{
			Empire e = new Empire(names[i], palette[i]);
			g.empires.add(e);

			Star s = g.stars.get(nascentGalaxy.startingLocations.get(i));
			s.setParameters(0.5f, 0.5f, 0.5f);
			
			Fleet f = new Fleet(s, e);
			f.addUnits(Unit.fetchByName("Scout"), 2);
			f.addUnits(Unit.fetchByName("Colony Ship"), 1);
			
			new Colony(s, e);
			new Shipyard(s);
		}
		g.playerEmpire = g.empires.get(0);
		return true;
	}

}
