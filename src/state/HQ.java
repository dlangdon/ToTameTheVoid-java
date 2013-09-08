/**
 * 
 */
package state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Image;

/**
 * @author Daniel Langdon
 */
public abstract class HQ extends Orbiter
{
// Statics ============================================================================================================
	/**
	 * An internal class in order to keep count of unit selection.
	 */
	public static class QueuedUnit
	{
		/**
		 * Number of units remaining, note that this is not an integer, as a unit may be partially built.
		 */
		public double queued;
		public Unit design;
	}

	private static HashSet<HQ> all_ = new HashSet<HQ>();

	public static HashSet<HQ> all()
	{
		return all_;
	}
	
// Internals ==========================================================================================================
	private LinkedList<Star> relocation_;		// Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.
	private List<QueuedUnit> queue_;
	private int level_;
	private double outputConfig;
	
// Public Methods =====================================================================================================
	/**
	 * Constructor. 
	 */
	public HQ(Star location)
	{
		super(location);
		queue_ = new ArrayList<HQ.QueuedUnit>(5);
		relocation_ = new LinkedList<Star>();
		level_ = 1;
		location.arrive(this);
		outputConfig = 1.0;
		all().add(this);
	}
	
	/**
	 * Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.
	 * @return A modifiable list of stars.
	 */
	public LinkedList<Star> getRelocation()
	{
		return relocation_;
	}

	/**
	 * @return the queue
	 */
	public List<QueuedUnit> queue()
	{
		return queue_;
	}

	/**
	 * Advances time for this HQ b building the unit in the queue.
	 */
	public void turn()
	{
		// Produce new units.
		Fleet newUnits = new Fleet(location_, location_.colony().owner());
		double toSpend = maxOutput()*outputConfig;
		while(!queue_.isEmpty() && toSpend > 0.0)
		{
			QueuedUnit qu = queue_.get(0);
			double maxCost = qu.design.cost() * qu.queued;
			if(maxCost <= toSpend)
			{
				newUnits.addUnits(qu.design, (int)Math.ceil(qu.queued));
				queue_.remove(0);
				toSpend -= maxCost;
			}
			else
			{
				int count = (int)Math.ceil(qu.queued);
				qu.queued -= toSpend/qu.design.cost();
				newUnits.addUnits(qu.design, count - (int)Math.ceil(qu.queued));
				toSpend = 0.0;
			}
		}
		location_.colony().owner().getEconomy().addMovement(toSpend - maxOutput()*outputConfig, outputExpense());

		// Set the fleet just created.
		if(!newUnits.isEmpty())
			for(Star s : relocation_)
				newUnits.addToRoute(s);
	}
	
	/**
	 * Level of this slot. The bigger the level, the more resources it can consume per turn.
	 */
	public int level()
	{
		return level_;
	}
	
	/**
	 * Type of hq, corresponding to the set of units it can produce.
	 */
	public abstract String getType();

	/**
	 * @return A list of all units that can be produced by this type of hq.
	 */
	public abstract List<Unit> availableUnits();

	/**
	 * @return A small (18x18) icon that corresponds to this hq.
	 */
	public abstract Image icon();

	public double maxOutput()
	{
		return Math.pow(2, level_);
	}
	
	void setOutputConfig(double percentage)
	{
		this.outputConfig = percentage;
	}
	
	double outputConfig()
	{
		return outputConfig;
	}

	protected abstract int outputExpense(); 
}
