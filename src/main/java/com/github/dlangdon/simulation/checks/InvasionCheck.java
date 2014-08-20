package com.github.dlangdon.simulation.checks;

import java.util.List;

import com.github.dlangdon.simulation.GameEvent;
import com.github.dlangdon.simulation.StarCheck;
import com.github.dlangdon.state.Colony;
import com.github.dlangdon.empire.Empire;
import com.github.dlangdon.state.Fleet;
import com.github.dlangdon.state.Star;

/**
 * Analyzes if an invasion should occur.
 * This assumes that any conflict in orbit have already been solved.
 * In case of multiple invading fleets, the most powerful one wins.
 * @author Daniel Langdon
 */
public class InvasionCheck implements StarCheck
{
	@Override
	public GameEvent check(Star location)
	{
		// If there is no colony, nothing to invade.
		Colony colony = location.getPlaceable(Colony.class);
		if(colony == null)
			return null;

		// Check if no fleets at all, or my own fleet in orbit protecting!
		List<Fleet> fleets = location.getFleets();
		if(fleets.isEmpty() || fleets.get(0).owner() == location.owner())
			return null;

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
		return null;
	}
}
