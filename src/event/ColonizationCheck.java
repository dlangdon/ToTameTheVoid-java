package event;

import java.util.List;
import java.util.Map.Entry;

import state.Colony;
import state.Empire;
import state.Fleet;
import state.Star;
import state.Unit;
import state.UnitStack;

/**
 * Eliminates empty fleets.
 * 
 * @author Daniel Langdon
 */
public class ColonizationCheck implements TurnSubProcess
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		// If there is a colony already, nothing to do.
		Colony colony = location.getColony();
		if (colony == null)
			return;

		List<Fleet> fleets = location.getFleetsInOrbit();
		Empire e = fleets.get(0).owner();
		UnitStack canColonize = null;
		for (Fleet f : fleets)
		{
			// Check if there are fleets from more than one empire.
			if(f.owner() != e)
				return;

			// Check if this fleets contains a colony ship.
			for(Entry<Unit, UnitStack> stack: f.stacks().entrySet())
			{
				// TODO This is hardcoded for now. Eventually we need to be able to discover which unit has special abilities of any kind.
				if(stack.getKey().name().compareToIgnoreCase("Colony Ship") == 0)
				{
					canColonize = stack.getValue();
					break;
				}
			}
		}
		
		// Create the option to colonize.
		if(canColonize != null)
		{
			queue.addEvent(new ColonizationEvent(location, canColonize, e));
		}
	}
}