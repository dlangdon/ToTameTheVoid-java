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
 * Checks if the option to colonize is available on a specific star. For this to
 * happen: <br>
 * 1.- The star needs to be unclaimed. <br>
 * 2.- Only a single empire can have fleets on top of it.
 * 
 * TODO Careful with influence units, which can so far be used to block stars
 * invisibly. In the future, the "blocking" behavior should be better defined.
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
		// We don't need previous colonization events.
		List<GameEvent> existing = queue.eventsForLocation(location);
		Iterator<GameEvent> i = existing.iterator();
		while (i.hasNext())
		{
			GameEvent e = i.next();
			if (e instanceof ColonizationEvent)
				i.remove();
		}

		// If there is a colony already, nothing to do.
		Colony colony = location.getPlaceable(Colony.class);
		if (colony != null)
			return;

		// Are there still fleets in orbit?
		List<Fleet> fleets = location.getFleets();
		if (fleets.isEmpty())
			return;

		Empire e = fleets.get(0).owner();
		Fleet canColonize = null;
		Unit toSpend = null;
		for (Fleet f : fleets)
		{
			// Check if there are fleets from more than one empire.
			if (f.owner() != e)
				return;

			// Check if this fleets contains a colony ship.
			for (Entry<Unit, UnitStack> stack : f.stacks().entrySet())
			{
				// TODO This is hardcoded for now. Eventually we need to be able to discover which unit has special abilities of any kind.
				if (stack.getKey().name().compareToIgnoreCase("Colony Ship") == 0)
				{
					canColonize = f;
					toSpend = stack.getKey();
					break;
				}
			}
		}

		// Create the option to colonize.
		if (canColonize != null)
		{
			queue.addEvent(new ColonizationEvent(location, canColonize, toSpend));
		}
	}
}