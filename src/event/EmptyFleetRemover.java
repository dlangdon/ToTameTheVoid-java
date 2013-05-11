package event;

import java.util.Iterator;

import state.Fleet;
import state.Star;
import state.Universe;

/**
 * Eliminates empty fleets.
 * @author Daniel Langdon
 */
public class EmptyFleetRemover implements TurnSubProcess
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void run(GameEventQueue queue)
	{
		for(Star s: Universe.instance().getStars())
		{
			// Checks for any empty fleet and removes it.
			Iterator<Fleet> i = s.getFleetsInOrbit().iterator();
			while(i.hasNext())
			{
				Fleet aux = i.next();
				if(aux.isEmpty())
				{
					i.remove();
					Universe.instance().removeFleet(aux);
				}
			}
		}
	}
}