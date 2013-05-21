package event;

import state.Fleet;
import state.Star;
import state.Universe;

/**
 * Updates locations of all fleets.
 * Note that a list of all fleets independent of their locations is needed, else we might update fleets that just arrived twice and have concurrency problems.
 * @author Daniel Langdon
 */
public class FleetUpdater implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		for(Fleet f: Universe.instance().getFleets())
			f.turn();
	}
}
