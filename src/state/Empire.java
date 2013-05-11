package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.newdawn.slick.Color;

public class Empire
{
// Statics ============================================================================================================	
	public static final int CEASE_FIRE = 17;
	public static final int OPEN_TRADE = 34;
	public static final int CULTURAL_COOPERATION = 50;
	public static final int SANCTUARY = 67;
	public static final int RESOURCE_SHARING = 84;
	
// Internals ==========================================================================================================
	private String name_;
	private Color color_;
	private Economy economy_;
	private HashSet<Colony> colonies_;
	private HashMap<Empire, Integer> trust;

// Public Methods =====================================================================================================

	public Empire(String name_, Color color_)
	{
		super();
		this.name_ = name_;
		this.color_ = color_;
		colonies_ = new HashSet<Colony>();
		economy_ = new Economy();
		trust = new HashMap<Empire, Integer>();
	}

	public void addColony(Colony colony)
	{
		colonies_.add(colony);
	}

	public String name()
	{
		return name_;
	}

	public Color color()
	{
		return color_;
	}
	
	void turn()
	{
		// Reset last's turn expenses.
		economy_.resetTurn();

		// Produce new expenses from all related modules.
		// Move all fleets.
//		foreach(Fleet* f, fleets)
//		{
//			f->turn();
//		}

		// Grow colonies and recalculate production.
		economy_.applyGrowth(colonies_);
	}
	
	/**
	 * @return The current economy state for this empire.
	 */
	public Economy getEconomy()
	{
		return economy_;
	}

	public Set<Colony> getColonies()
	{
		return colonies_;
	}

	/**
	 * Returns the level of trust this empire is willing to confer to the other empire.
	 * @param other Opposite empire.
	 * @return A number between 0 and 100 measuring trust.
	 */
	public int trustLevel(Empire other)
	{
		Integer aux = trust.get(other);
		return aux == null ? 0 : aux;
	}
	
	/**
	 * Returns the level of reciprocal truth between two empires.
	 * @param other The other empire.
	 * @return A number between 0 and 100 measuring trust.
	 */
	public int reciprocalTrust(Empire other)
	{
		return Math.min(this.trustLevel(other), other.trustLevel(this));
	}
}
