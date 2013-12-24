package state;

import empire.Empire;
import galaxy.structure.Placeable;
import org.newdawn.slick.Image;

public class Colony extends Placeable
{
// Internals ==========================================================================================================
	private static final float MIN_INFRASTRUCTURE = 0.01f;

	private double infrastructure_;
	private double maxInfrastructure_; // Only changes after tech updated, hence update off-turn.
	private double lastTurnProduction_;
	private double production_;
	private double maintenance_;
	private double conditions_;
	private double roi_;

// Public Methods =====================================================================================================
	public Colony(Star location, Empire empire)
	{
		super(location);
		location.setOwner(empire);
		empire.addColony(this);

		infrastructure_ = MIN_INFRASTRUCTURE; // This is the initial infrastructure after colonization.
		roi_ = 0;
		lastTurnProduction_ = 0.001f;
		conditions_ = location.conditions();

		// Run once to get valid values for the internal variables.
		updateTech();
		spend(1.0f, 0.0f, 9999, false);
	}

	/**
	 * Stores stats for next turn. Actual production is handled on spend()
	 */
	public void turn()
	{
		lastTurnProduction_ = production_;
	}

	public void updateTech()
	{
		maxInfrastructure_ = (float) ((0.75f + star().size()) * ((conditions_ + 0.75f) - Math.sqrt((conditions_ + 0.75f) / (star().resources() + 0.75f))));
		if (maxInfrastructure_ <= 0)
			maxInfrastructure_ = MIN_INFRASTRUCTURE;
	}

	/**
	 * Note that the implementation is only an approximation. It uses previously calculated ROI as the expected ROI for
	 * all the purchased infrastructure, without interpolations or contiguous updating. The same principle is generally
	 * used, as to avoid having to iterate over the infrastructure being built.
	 * 
	 * @brief Instructs the colony to spend in more infrastructure.
	 * @param percentage
	 *           Percentage of local production to spend.
	 * @param maximum
	 *           Maximum total cost of new infrastructure.
	 * @param ROILimit
	 *           Maximum number of turns before the infrastructure pays for itself.
	 * @param nonLocal
	 *           True if the colony should use imported resources, which cost twice as much per unit of infrastructure.
	 * @return Total cost of infrastructure actually built, after applying all spending restrictions.
	 */
	public double spend(double percentage, double maximum, int ROILimit, boolean nonLocal)
	{
		// Avoid infrastructure that is too costly.
		if (roi_ >= ROILimit || lastTurnProduction_ <= 0)
			return 0.0;

		// Calculate real cost of new infrastructure.
		double cost = (2.0 + 2.5 / (conditions_ + 0.75));
		if (nonLocal)
			cost *= 2;

		// We can't spend more than the allowed percentage of last year's production.
		if (maximum > lastTurnProduction_ * percentage)
			maximum = lastTurnProduction_ * percentage;

		// We can't grow past the maximum infrastructure limit, else we risk decreasing total production.
		double toGrow = maxInfrastructure_ - infrastructure_;
		if (toGrow * cost > maximum)
			toGrow = maximum / cost;

		// Invest in infrastructure
		infrastructure_ += toGrow;
		cost = toGrow * cost;

		// Recalculate economy due to last turn spending.
		conditions_ = star().conditions() - infrastructure_ / (star().size() + 0.75f);
		double auxMaintenance = infrastructure_ / (conditions_ + 0.75);
		double auxProduction = infrastructure_ * (star().resources() + 0.75);
		if (cost > 0)
			roi_ = cost / (auxProduction - auxMaintenance - production_ + maintenance_);
		production_ = auxProduction;
		maintenance_ = auxMaintenance;

		return cost;
	}

	/**
	 * @brief killInfrastructure Destroys a certain amount of infrastructure.
	 * @param amount
	 *           Amount of infrastructure to destroy. Excedents are ignored.
	 * @return True if the infrastructure went down to 0 (and the colony should be deleted).
	 */
	public boolean killInfrastructure(double amount)
	{
		if (amount > infrastructure_)
		{
			infrastructure_ = 0.0;
			return true;
		}
		infrastructure_ -= amount;
		return false;
	}
	
 	public double infrastructure()
	{
		return infrastructure_;
	}

	public double maxInfrastructure()
	{
		return maxInfrastructure_;
	}

	/**
	 * @return The last calculated total production of this colony. This value does not discount maintenance.
	 */
	public double production()
	{
		return production_;
	}

	public double maintenance()
	{
		return maintenance_;
	}

	public double conditions()
	{
		return conditions_;
	}

	public double returnOfInvestment()
	{
		return roi_;
	}

	/* (non-Javadoc)
	 * @see galaxy.structure.Placeable#icon()
	 */
	@Override
	public Image icon()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see galaxy.structure.Placeable#priority()
	 */
	@Override
	public int priority()
	{
		return 0;
	}

}
