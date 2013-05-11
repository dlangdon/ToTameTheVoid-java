package event;

import state.Empire;
import state.Universe;

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
	public void run(GameEventQueue queue)
	{
		for(Empire e: Universe.instance().getEmpires())
		{
			e.getEconomy().resetTurn();
		}
	}
}