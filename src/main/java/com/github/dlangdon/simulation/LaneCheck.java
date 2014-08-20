/**
 *
 */
package com.github.dlangdon.simulation;

import com.github.dlangdon.state.Lane;

/**
 * Interface for game event tests and creation.
 * @author Daniel Langdon
 */
public interface LaneCheck
{
	/**
	 * Test a location for potential conflicts.
	 * If they can be solved immediately, they should. Else, GameEvents might be created to be solved with user input later on or even concurrently.
	 * @param location The location to test for conflicts.
	 */
	public GameEvent check(Lane location);
}
