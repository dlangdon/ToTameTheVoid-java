package empire;

import state.Colony;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents the economy of an empire, including growth policy, reserves and turn accounting.
 * Different game modules can post expenses to the economy, both positive and negative. 
 * A static list of expense causes needs to be statically created when modules are loaded, so multiple expenses can be added to a single cause by different objects. 
 * The reason for this static initialization is that the economy module does not have to have prior knowledge of which other modules exist. 
 * 
 * @author Daniel Langdon
 */
public class Economy
{
// Statics ==========================================================================================================	
	private static ArrayList<String> causes_;

	/**
	 * Global initialization phase to produce module constants, registration with other modules, resource loading, etc.
	 */
	public static void init()
	{
		causes_ = new ArrayList<String>();
		registerCause("Colony Maintenance");
		registerCause("Colony Growth");
		registerCause("Total Production");
	}

	/**
	 * Registers a possible cause for income or expenses for the economy. On each turn, multiple movements can be made due to a single cause, and they will be grouped together.
	 * @param description The description to which all movements will be added.
	 * @return A new identifier for the registered cause.
	 */
	public static int registerCause(String description)
	{
		causes_.add(description);
		return causes_.size()-1;
	}
	
	/**
	 * @return A list of all currently registered causes for economic movements (either positive or negative).
	 */
	public static ArrayList<String> causes()
	{
		return causes_;
	}
	
	
// Internals ==========================================================================================================	
	private float totalInfrastructure_;
	private float totalProduction_;
	private float totalGrowth_;
	private float totalMaintenance_;
	private double bestROI_;
	private float reserve_;
	private float growthPolicy_;
	private int returnOfInvestmentLimit_;
	private boolean onlyLocal_;
	private float[] movements_;
	private boolean[] rejections_;

	// Public Methods =====================================================================================================
	public Economy()
	{
		totalInfrastructure_ = 0;
		totalGrowth_ = 0;
		totalProduction_ = 0.01f;						// Initial production for turn 1
		reserve_ = 0.1f;									// Need to start with some money, else we can't pay first's turns expenses.
		bestROI_ = 9999;
		growthPolicy_ = 0.5f;
		returnOfInvestmentLimit_ = 9999;
		onlyLocal_ = true;
		movements_ = new float[causes_.size()];
		rejections_ = new boolean[causes_.size()];
	}
	
	public float growthPolicy()
	{
		return growthPolicy_;
	}

	public void setGrowthPolicy(float growthPolicy_)
	{
		this.growthPolicy_ = growthPolicy_;
	}

	public int returnOfInvestmentLimit()
	{
		return returnOfInvestmentLimit_;
	}

	public void setReturnOfInvestmentLimit(int returnOfInvestmentLimit_)
	{
		this.returnOfInvestmentLimit_ = returnOfInvestmentLimit_;
	}

	public boolean isOnlyLocal()
	{
		return onlyLocal_;
	}

	public void setOnlyLocal(boolean onlyLocal_)
	{
		this.onlyLocal_ = onlyLocal_;
	}

	public float totalInfrastructure()
	{
		return totalInfrastructure_;
	}

	public float totalProduction()
	{
		return totalProduction_;
	}

	public float totalGrowth()
	{
		return totalGrowth_;
	}

	public float totalMaintenance()
	{
		return totalMaintenance_;
	}

	public double bestROI()
	{
		return bestROI_;
	}

	public float reserve()
	{
		return reserve_;
	}

	/**
	 * Tries to apply the specified amount to the reserve. If the amount would made the reserve go below 0, the movement is rejected.
	 * Modules can use reserve() to check before calling, or handle the failure case.
	 * @param d Amount of credits to be added or removed from the reserve, a positive value is considered addition, a negative one subtraction.
	 * @param cause Pre-registered bin to which add the movement.
	 * @return True if the reserve could process the movement, else 0.
	 */
	public boolean addMovement(double d, int cause)
	{
		if(d + reserve_ < 0 || Double.isNaN(d))
		{
			rejections_[cause] = true;
			return false;
		}

		movements_[cause] += d;
		reserve_ += d;
		return true;
	}

	/**
	 * @param colonies
	 *
	 * The function acts in four steps.
	 * The first pass guarantees that every colony has some chance to grow, by letting it apply the policy locally
	 * (i.e: apply 30% of total local production).
	 * During the pass, the colonies are sorted by cost of new infrastructure.
	 * Colonies whose return of investment is greater than the limit are ignored.
	 *
	 * On the second pass, we let them spend as much local production as they have left, cheapest colonies first, until the money runs out.
	 * (i.e: spending the other 70%). Colonies are re-sorted with their updated costs.
	 *
	 * On the third pass, if non-local investments are allowed and we have money still, we repeat the second phase,
	 * but now for the extra 100% production that a colony is able to handle over their own local production.
	 *
	 * Finally, the turn is advanced and new values for all economic variables calculated.
	 * Note that all sources of income or expenses that depend on these values should be called before this method is invoked. In general is a good policy to leave colony growth for last.
	 */
	public void applyGrowth(Set<Colony> colonies)
	{
		// Before anything can be done, pay for maintenance for the current level of infrastructure.
		if(!addMovement(-totalMaintenance_, 0))
		{
			// If the cost of infrastructure is not paid, infrastructure is reduced by 50% of the proportion between the cost paid and the unpaid.
			// Note that this will never kill any colony ;-)
			double percentage = (1.0 - reserve_/totalInfrastructure_) * 0.5;
			addMovement(-reserve_, 0);
			for(Colony colony : colonies)
				colony.spend(-percentage, 0.0, 9999, false);
		}
		else
		{
			// Initialize the credit pool to spend based on the policy and how much money we actually have.
			float reminder = totalProduction_ * growthPolicy_;
			if(!addMovement(-reminder, 1))
			{
				reminder = reserve_;
				addMovement(-reminder, 1);
			}
	
			// First pass: let every system apply the policy locally.
			TreeMap<Double, Colony> firstBets = new TreeMap<Double, Colony>();
			for(Colony colony : colonies)
			{
				if(reminder < 1e-6)
					break;
	
				reminder -= colony.spend(growthPolicy_, reminder, returnOfInvestmentLimit_, false);
				firstBets.put(colony.returnOfInvestment(), colony);
			}
	
			// Second pass: take the remainder and apply it, cheapest colonies first, till the money runs out.
			TreeMap<Double, Colony> secondBets = new TreeMap<Double, Colony>();
			for(Colony colony : firstBets.values())
			{
				if(reminder < 1e-6)
					break;
	
				reminder -= colony.spend(1.0f - growthPolicy_, reminder, returnOfInvestmentLimit_, false);
				secondBets.put(colony.returnOfInvestment(), colony);
			}
	
			// Third pass: if non-local growth is allowed, repeat the second step
			if(!onlyLocal_)
				for(Colony colony : secondBets.values())
				{
					if(reminder < 1e-6)
						break;
	
					reminder -= colony.spend( 1.0f, reminder, returnOfInvestmentLimit_, true);
				}

			// Adjust the expense if we have money left. Should mainly happen when ROI is very restricted or most colonies are at full infrastructure.
			addMovement(reminder, 1);
		}
		
		// Pass the turn for each colony and update totals.
		float prodCount = 0.0f;
		totalMaintenance_ = 0.0f;
		bestROI_ = 9999;
		totalInfrastructure_ = 0.0f;
		for(Colony colony : colonies)
		{
			colony.turn();
			prodCount += colony.production();
			totalInfrastructure_ += colony.infrastructure();
			totalMaintenance_ += colony.maintenance();

			double roi = colony.returnOfInvestment();
			if(roi < bestROI_ )
				bestROI_ = roi;
		}

		totalGrowth_ = prodCount - totalProduction_;
		totalProduction_ = prodCount;
	}

	/**
	 * Advances the state of the economy to the next turn.
	 */
	public void resetTurn()
	{
		// Reset turn counters and get ready to accept expenses.
		for(int i=0; i<causes_.size(); i++)
		{
			movements_[i] = 0.0f;
			rejections_[i] = false;
		}
		
		// Raise production to the reserve.
		addMovement(totalProduction_, 2);
	}
	
	/**
	 * @return A list with all the movements done during the turn. It should not be modified externally.
	 */
	public float[] movements()
	{
		return movements_;
	}
	
	public boolean[] rejections()
	{
		return rejections_;
	}
}
