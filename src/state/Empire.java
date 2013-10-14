package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.newdawn.slick.Color;

public class Empire
{
// Statics ============================================================================================================	
	public static final float DISTRUST = 0.0f;
	public static final float CEASE_FIRE = 0.14f;
	public static final float OPEN_TRADE = 0.28f;
	public static final float CULTURAL_COOPERATION = 42f;
	public static final float SANCTUARY = 0.57f;
	public static final float RESOURCE_SHARING = 71f;
	public static final float OPEN_BORDERS = 85f;
	public static final float BROTHERHOOD = 1.0f;
	
// Internals ==========================================================================================================
	private String name_;
	private Color color_;
	private Economy economy_;
	private HashSet<Colony> colonies_;
	private HashMap<Empire, Double> trust;

// Public Methods =====================================================================================================

	public Empire(String name_, Color color_)
	{
		super();
		this.name_ = name_;
		this.color_ = color_;
		colonies_ = new HashSet<Colony>();
		economy_ = new Economy();
		trust = new HashMap<Empire, Double>();
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
	public double trustLevel(Empire other)
	{
		if(this == other)
			return 100;

		Double aux = trust.get(other);
		return aux == null ? 0 : aux;
	}
	
	/**
	 * Returns the level of reciprocal truth between two empires.
	 * @param other The other empire.
	 * @return A number between 0 and 100 measuring trust.
	 */
	public double reciprocalTrust(Empire other)
	{
		return Math.min(this.trustLevel(other), other.trustLevel(this));
	}
}
