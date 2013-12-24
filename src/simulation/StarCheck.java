/**
 * 
 */
package simulation;

import state.Star;


/**
 * Interface for game event tests and creation.
 * @author Daniel Langdon
 */
public interface StarCheck
{
	/**
	 * Test a location for potential conflicts. 
	 * If they can be solved immediately, they should. Else, GameEvents might be created to be solved with user input later on or even concurrently.
	 * @param location The location to test for conflicts.
	 */
	public GameEvent check(Star location);
}
