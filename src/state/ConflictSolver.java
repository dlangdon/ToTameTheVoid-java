/**
 * 
 */
package state;

/**
 * Interface for game event tests and creation.
 * @author Daniel Langdon
 */
public interface ConflictSolver
{
	/**
	 * Test a location for potential conflicts. 
	 * If they can be solved immediately, they should. Else, GameEvents might be created to be solved with user input later on or even concurrently.
	 * @param location The location to test for conflicts.
	 * @param queue Event queue for conflicts that could not be solved automatically.
	 */
	public void checkForEvents(Star location, GameEventQueue queue);
}
