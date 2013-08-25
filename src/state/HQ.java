/**
 * 
 */
package state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import military.Ship;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

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
		public float queued;
		public Unit design;
	}

// Internals ==========================================================================================================
	private LinkedList<Star> relocation_;		// Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.
	private LinkedList<QueuedUnit> queue_;
	private int level_;
	
// Public Methods =====================================================================================================
	/**
	 * Constructor. 
	 */
	public HQ(Star location)
	{
		super(location);
		relocation_ = new LinkedList<Star>();
		level_ = 1;
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
	public LinkedList<QueuedUnit> queue()
	{
		return queue_;
	}

	/**
	 * Advances time for this HQ.
	 */
	public void turn()
	{
		// Process upgrades
		// Create ships
		// Create expenses
		// TODO
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

}
