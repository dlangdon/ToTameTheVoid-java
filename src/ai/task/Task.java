/**
 * 
 */
package ai.task;

/**
 * @author Daniel Langdon
 */
public interface Task
{
	double importance();
	double fitness(Object doer);
	void execute(Object doer);
}
