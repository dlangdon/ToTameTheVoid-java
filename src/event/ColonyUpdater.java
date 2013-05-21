package event;

import state.Empire;
import state.Star;
import state.Universe;

/**
 * Eliminates empty fleets.
 * @author Daniel Langdon
 */
public class ColonyUpdater implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		for(Empire e: Universe.instance().getEmpires())
		{
			e.getEconomy().applyGrowth(e.getColonies());
		}
	}
}