package military;

import java.util.List;

import state.Colony;
import state.Empire;
import state.Fleet;
import state.Star;
import event.GameEventQueue;
import event.TurnSubProcess;

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
	public void check(GameEventQueue queue, Star location)
	{
		// If there is no colony, nothing to invade.
		Colony colony = location.getPlaceable(Colony.class);
		if(colony == null)
			return;				
		
		// Check if no fleets at all, or my own fleet in orbit protecting!
		List<Fleet> fleets = location.getFleets();
		if(fleets.isEmpty() || fleets.get(0).owner() == location.owner())
			return;				

		// Now, collect all possible invaders.
		// TODO for now this is trivial, invasion is FIFO and automatic.
		for(Fleet f : fleets)
		{
			if(f.owner().reciprocalTrust(location.owner()) < Empire.CEASE_FIRE)
			{
				// Conquered!
				location.setOwner(f.owner());
				break;
			}
		}
	}
}
