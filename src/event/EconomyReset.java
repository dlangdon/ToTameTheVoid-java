package event;

import galaxy.generation.Galaxy;
import state.Empire;
import state.Star;

/**
 * Eliminates empty fleets.
 * @author Daniel Langdon
 */
public class EconomyReset implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		for(Empire e: Galaxy.instance().getEmpires())
		{
			e.getEconomy().resetTurn();
		}
	}
}