package state;

public class Colony
{
// Internals ==========================================================================================================
	private static final float MIN_INFRASTRUCTURE = 0.01f;

	private float infrastructure_;
	private float maxInfrastructure_; // Only changes after tech updated, hence update off-turn.
	private float lastTurnProduction_;
	private float production_;
	private float maintenance_;
	private float conditions_;
	private float roi_;
	private Star location_;
	private Empire owner_;

// Public Methods =====================================================================================================
	public Colony(Star location, Empire empire)
	{
		location_ = location;
		owner_ = empire;
		owner_.addColony(this);
		location.setColony(this);

		infrastructure_ = MIN_INFRASTRUCTURE; // This is the initial infrastructure after colonization.
		roi_ = 0;
		lastTurnProduction_ = 0.001f;
		conditions_ = location.conditions();

		// Run once to get valid values for the internal variables.
		updateTech();
		spend(1.0f, 0.0f, 9999, false);
	}

	// / @todo Agregar desición de que hacer con la producción, por ahora solo crecer.
	public void turn()
	{
		lastTurnProduction_ = production_;
	}

	public void updateTech()
	{
		maxInfrastructure_ = (float) ((0.75f + location_.size()) * ((conditions_ + 0.75f) - Math.sqrt((conditions_ + 0.75f) / (location_.resources() + 0.75f))));
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
	public float spend(float percentage, float maximum, int ROILimit, boolean nonLocal)
	{
		// Avoid infrastructure that is too costly.
		if (roi_ >= ROILimit || lastTurnProduction_ <= 0)
			return 0.0f;

		// Calculate real cost of new infrastructure.
		float cost = (2.0f + 2.5f / (conditions_ + 0.75f));
		if (nonLocal)
			cost *= 2;

		// We can't spend more than the allowed percentage of last year's production.
		if (maximum > lastTurnProduction_ * percentage)
			maximum = lastTurnProduction_ * percentage;

		// We can't grow past the maximum infrastructure limit, else we risk decreasing total production.
		float toGrow = maxInfrastructure_ - infrastructure_;
		if (toGrow * cost > maximum)
			toGrow = maximum / cost;

		// Invest in infrastructure
		infrastructure_ += toGrow;
		cost = toGrow * cost;

		// Recalculate economy due to last turn spending.
		conditions_ = location_.conditions() - infrastructure_ / (location_.size() + 0.75f);
		float auxMaintenance = infrastructure_ / (conditions_ + 0.75f);
		float auxProduction = infrastructure_ * (location_.resources() + 0.75f);
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
	public boolean killInfrastructure(float amount)
	{
		if (amount > infrastructure_)
		{
			infrastructure_ = 0;
			return true;
		}
		infrastructure_ -= amount;
		return false;
	}
	
	public float infrastructure()
	{
		return infrastructure_;
	}

	public float maxInfrastructure()
	{
		return maxInfrastructure_;
	}

	/**
	 * @return The last calculated total production of this colony. This value does not discount maintenance.
	 */
	public float production()
	{
		return production_;
	}

	public float maintenance()
	{
		return maintenance_;
	}

	public float conditions()
	{
		return conditions_;
	}

	public float returnOfInvestment()
	{
		return roi_;
	}

	public Empire owner()
	{
		return owner_;
	}
}
