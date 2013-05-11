package state;

import java.util.HashSet;
import java.util.Set;

import org.newdawn.slick.Color;

public class Empire
{
// Internals ==========================================================================================================
	private String name_;
	private Color color_;
	private Economy economy_;
	private HashSet<Colony> colonies_;

// Public Methods =====================================================================================================

	public Empire(String name_, Color color_)
	{
		super();
		this.name_ = name_;
		this.color_ = color_;
		colonies_ = new HashSet<Colony>();
		economy_ = new Economy();
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

}
