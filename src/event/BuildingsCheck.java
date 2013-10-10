package event;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import state.Colony;
import state.Empire;
import state.Fleet;
import state.Star;
import state.Unit;
import state.UnitStack;

/**
 * Checks if a certain building of type T can be built in the star. Where type T is any kind of building.
 * This check and corresponding event are generic so they can handle the construction of all kinds of buildings.
 * @author Daniel Langdon
 */
public class BuildingsCheck<T> implements TurnSubProcess
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		// Check if the building can still be made.
		
		// Check if we had such an event before.
		
		// Delete it if no longer valid.
		
		// Create a new one if needed.
		
		
		// We don't need previous colonization events.
		List<GameEvent> existing = queue.eventsForLocation(location);
		Iterator<GameEvent> i = existing.iterator();
		while(i.hasNext())
		{
			GameEvent e = i.next();
			if(e instanceof ColonizationEvent)
				i.remove();
		}
		
		// If there is a colony already, nothing to do.
		Colony colony = location.colony();
		if (colony != null)
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
//			queue.addEvent(new ColonizationEvent(location, canColonize, e));
		}
	}
}