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
public abstract class HQ
{
// Statics ============================================================================================================
	private static int maintenanceExpense;
	private static int upgradeExpense;
	private static int constructionExpense;
	private static List<Unit> availableUnits_;
	private static Image icon_;

	/**
	 * Global initialization phase to produce module constants, registration with other modules, resource loading, etc.
	 */
	public static void init()
	{
		maintenanceExpense = Economy.registerCause("IHQ Maintenance");
		upgradeExpense = Economy.registerCause("IHQ Expansion");
		constructionExpense = Economy.registerCause("Fleet Construction");
	
		availableUnits_ = new ArrayList<Unit>();
		
		try
		{
			// Add units.
			// TODO For now, just a hardcoded list of units.
			availableUnits_.add(new Ship("Figther", new Image("resources/ship2.png")));
			availableUnits_.add(new Ship("Colony Ship", new Image("resources/ship1.png")));
			
			// Load icon.
			icon_ = new Image("resources/ironFist.png");
		}
		catch (SlickException e)
		{
			System.out.println("Problem initializing resources.");
			e.printStackTrace();
		}
	}
	
	/**
	 * An internal class in order to keep count of unit selection.
	 */
	public static class QueuedUnit
	{
		public float queued;
		public Unit design;
	}

// Internals ==========================================================================================================
	private Colony colony_;							// Empire that owns this Fleet.
	private LinkedList<Star> relocation_;		// Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.
	private LinkedList<QueuedUnit> queue_;
	private int level_;
	
// Public Methods =====================================================================================================
	/**
	 * Constructor. 
	 */
	public HQ(Colony colony)
	{
		colony_ = colony;
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
	 * @return the colony where this HQ has been created.
	 */
	public Colony colony()
	{
		return colony_;
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
