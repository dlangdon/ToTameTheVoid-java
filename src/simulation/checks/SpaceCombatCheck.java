package simulation.checks;

import java.util.ArrayList;
import java.util.List;

import military.SpaceCombatSimulation;
import empire.Empire;
import state.Fleet;
import state.Star;

/**
 * Creates events for space battles.
 * @author Daniel Langdon
 */
public class SpaceCombatCheck
{
	public void check(Star location)
	{
		// Get the list of fleets really in conflict. Remove fleets that have no hostile opponents (hence would not engage at all).
		List<Fleet> fleets = new ArrayList<>();
		for(Fleet f: location.getFleets())
		{
			// Empty fleet is ignored.
			if(f.isEmpty())
				continue;

			// Check if this fleet is in conflict with any other. (not too optimal, but fleet list is supposed to be pretty small.
			for(Fleet f2: location.getFleets())
			{
				if(!f2.isEmpty() && f2.owner().reciprocalTrust(f.owner()) < Empire.CEASE_FIRE)
				{
					fleets.add(f);
					break;
				}
			}
		}

		SpaceCombatSimulation sim = new SpaceCombatSimulation(fleets);
		sim.run();
	}
}