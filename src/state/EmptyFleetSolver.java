/**
 * 
 */
package state;

import java.util.Iterator;

/**
 * Eliminates empty fleets.
 * @author Daniel Langdon
 */
public class EmptyFleetSolver implements ConflictSolver
{
	/* (non-Javadoc)
	 * @see state.ConflictSolver#checkForEvents(state.Star, state.GameEventQueue)
	 */
	@Override
	public void checkForEvents(Star location, GameEventQueue queue)
	{
		// Checks for any empty fleet and removes it.
		Iterator<Fleet> i = location.getFleetsInOrbit().iterator();
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