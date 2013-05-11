package event;

import java.util.List;

import state.Fleet;
import state.Star;
import state.Universe;

/**
 * Creates events for space battles.
 * @author Daniel Langdon
 */
public class SpaceCombatCheck implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void run(GameEventQueue queue)
	{
		for(Star s: Universe.instance().getStars())
		{
			// TODO Re-make list with fleet type and not empty. 
			List<Fleet> fleets = s.getFleetsInOrbit();

			SpaceCombatSimulation sim = new SpaceCombatSimulation(fleets);
			sim.step();
		}
	}
}