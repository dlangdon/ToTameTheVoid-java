/**
 * 
 */
package ai.task;

import state.Star;

/**
 * A task that can be executed.
 * @author Daniel Langdon
 */
public interface Task
{
	Star location();
	double importance();
	double fitness(Object doer);
	void execute(Object doer);
}
