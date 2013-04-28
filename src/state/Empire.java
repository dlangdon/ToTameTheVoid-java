package state;

import java.util.HashSet;

import org.newdawn.slick.Color;

public class Empire
{
// Internals ==========================================================================================================
	private String name_;
	private Color color_;
	Economy economy_;
	// QSet<Fleet*> fleets;
	private HashSet<Colony> colonies;

// Public Methods =====================================================================================================

	public Empire(String name_, Color color_)
	{
		super();
		this.name_ = name_;
		this.color_ = color_;
		colonies = new HashSet<Colony>();
		economy_ = new Economy();
	}

	public void addColony(Colony colony)
	{
		colonies.add(colony);
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
		economy_.prepareTurn();

		// Produce new expenses from all related modules.
		// Move all fleets.
//		foreach(Fleet* f, fleets)
//		{
//			f->turn();
//		}

		// Grow colonies and recalculate production.
		economy_.applyGrowth(colonies);
	}

}
