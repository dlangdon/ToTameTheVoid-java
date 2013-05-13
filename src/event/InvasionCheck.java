package event;

import java.util.List;

import state.Colony;
import state.Empire;
import state.Fleet;
import state.Star;
import state.Universe;

/**
 * Analyzes if an invasion should occur. 
 * This assumes that any conflict in orbit have already been solved.
 * In case of multiple invading fleets, the most powerful one wins.
 * @author Daniel Langdon
 */
public class InvasionCheck implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void run(GameEventQueue queue)
	{
		for(Star s: Universe.instance().getStars())
		{
			// If there is no colony, nothing to invade.
			Colony colony = s.getColony();
			if(colony == null)
				continue;				
			
			// Check if no fleets at all, or my own fleet in orbit protecting!
			List<Fleet> fleets = s.getFleetsInOrbit();
			if(fleets.isEmpty() || fleets.get(0).owner() == colony.owner())
				continue;				

			// Now, collect all possible invaders.
			// TODO for now this is trivial, invasion is FIFO and automatic.
			for(Fleet f : fleets)
			{
				if(f.owner().reciprocalTrust(colony.owner()) < Empire.CEASE_FIRE)
				{
					// Conquered!
					colony.setOwner(f.owner());
					break;
				}
			}
		}
	}
}