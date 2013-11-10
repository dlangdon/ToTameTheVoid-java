/**
 * 
 */
package ai.task;

import java.util.List;

import state.Empire;
import state.Star;

/**
 * Task to colonize a potential colony.
 * These correspond to an unclaimed star, adjacent to an existing empire colony.
 * @author Daniel Langdon
 */
public class Colonization implements Task
{
	/**
	 * Finds all colonization tasks available to a given empire.
	 * These are defined as all unclaimed stars
	 */
	static List<Task> findTasksForEmpire(Empire e)
	{
		return null;
		
		
		// Check if this fleets contains a colony ship.
//		for (Entry<Unit, UnitStack> stack : f.stacks().entrySet())
//		{
//			// TODO This is hardcoded for now. Eventually we need to be able to discover which unit has special abilities of any kind.
//			if (stack.getKey().name().compareToIgnoreCase("Colony Ship") == 0)
//			{
//				canColonize = f;
//				toSpend = stack.getKey();
//				break;
//			}
//		}

	}
	
	Star colonize;
	
	/* (non-Javadoc)
	 * @see ai.task.Task#importance()
	 */
	@Override
	public double importance()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ai.task.Task#fitness(java.lang.Object)
	 */
	@Override
	public double fitness(Object doer)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see ai.task.Task#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object doer)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ai.task.Task#location()
	 */
	@Override
	public Star location()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
