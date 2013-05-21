/**
 * 
 */
package event;

import state.Star;


/**
 * Interface for game event tests and creation.
 * @author Daniel Langdon
 */
public interface TurnSubProcess
{
	enum Priority { PREUPDATE, UPDATE, POSTUPDATE, CONFLICT }
	
	/**
	 * Test a location for potential conflicts. 
	 * If they can be solved immediately, they should. Else, GameEvents might be created to be solved with user input later on or even concurrently.
	 * @param location The location to test for conflicts.
	 * @param queue Event queue for conflicts that could not be solved automatically.
	 */
	public void check(GameEventQueue queue, Star location);
	
	/**
	 * Returns a priority for this process. Higher priority processes are run first every turn. 
	 * @return A numeric value for the priority of this process. 
	 */
	// public int priority();
}
