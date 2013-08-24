/**
 * 
 */
package state;

import java.util.LinkedList;

/**
 * An Imperial Headquarters.
 * An IHQ is a center to pool resources to, in order to build units. 
 * @author Daniel Langdon
 */
public class ImperialHQ
{
// Statics ============================================================================================================
	
	private static int maintenanceExpense;
	private static int upgradeExpense;
	private static int constructionExpense;
	
	/**
	 * Global initialization phase to produce module constants, registration with other modules, resource loading, etc.
	 */
	public static void init()
	{
		maintenanceExpense = Economy.registerCause("IHQ Maintenance");
		upgradeExpense = Economy.registerCause("IHQ Expansion");
		constructionExpense = Economy.registerCause("Fleet Construction");
	}

// Internals ==========================================================================================================
	private IHQSlot[] slots_;						// The 3 or 4 slots per IHQ.
	private Empire owner_;							// Empire that owns this Fleet.
	private LinkedList<Star> relocation;		// Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.

// Public Methods =====================================================================================================
	/**
	 * Constructor. 
	 */
	public ImperialHQ()
	{
		relocation = new LinkedList<Star>();
		slots_ = new IHQSlot[4];
	}
	
	/**
	 * Route to be followed by fleets created by this IHQ. The first star corresponds to the location of the IHQ.
	 * @return A modifiable list of stars.
	 */
	public LinkedList<Star> getRelocation()
	{
		return relocation;
	}


	/**
	 * @return the slots
	 */
	public IHQSlot[] slots()
	{
		return slots_;
	}

}
