package event;

import galaxy.generation.Galaxy;
import state.Fleet;
import state.Star;

/**
 * Eliminates empty fleets.
 * @author Daniel Langdon
 */
public class EmptyFleetRemover implements TurnSubProcess
{
	@Override
	public void check(GameEventQueue queue, Star location)
	{
		for(Fleet f : location.getFleetsInOrbit())
			if(f.isEmpty())
				Galaxy.instance().removeFleet(f);
	}
}