/**
 * 
 */
package ai;

import java.util.HashMap;
import java.util.List;

import state.Empire;
import state.Fleet;
import state.Star;

/**
 * A view of the current state of the galaxy, as seen by a single empire.
 * This is a highly skewed view, as each empire will only have partial knowledge about what is going on, both due to fog of war and invisible units.
 * It is more efficient to update this view every time a turn begins, since we can distribute objects from the true state to each empire's view without having to go through it multiple times.
 * @author Daniel Langdon
 */
public class EmpireView
{
	private static HashMap<Empire, EmpireView> views = new HashMap<Empire, EmpireView>();
	
	/**
	 * Refresh the current view according to the current 
	 */
	public static void refreshAllViews()
	{
		for(Star s: Star.all())
		{
			
		}
		
		views.clear();
	}
	
	private List<Star> myTerritory;
	private List<Star> unclaimedTerritory;
	private List<Star> friendlyTerritory;
	private List<Star> enemyTerritory;
	private List<Star> unexploredTerritory;

	private List<Fleet> myFleets;
	private List<Fleet> enemyFleets;
}
