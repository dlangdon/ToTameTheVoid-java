package event;

import java.util.List;

import state.Fleet;
import state.Star;

/**
 * Analyzes potential fleet merges and performs them.
 * @author Daniel Langdon
 */
public class FleetMerger implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		List<Fleet> fleets = location.getFleetsInOrbit();

		// Check for fleets to merge.
		for(int i=0; i<fleets.size(); i++)
		{
			Fleet a = fleets.get(i);
			for(int j= i+1; j<fleets.size(); j++)
			{
				Fleet b = fleets.get(j);
				
				// Fleets are assumed to be sorted by empire, then type, we can stop checking if we find otherwise.
				if(a.owner() != b.owner() || a.type() != b.type())
					break;

				// Check if this particular fleet should be merged with A.
				// TODO Other tests while fleets should not be merged: named fleets?
				if(!a.hasOrders() && !b.hasOrders())
				{
					b.mergeIn(a);
				}
			}
		}
	}
}
