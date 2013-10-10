/**
 * 
 */
package state;

/**
 * Helper class to store temporary status for a stack of ships.
 * @author Daniel Langdon
 */
public class UnitStack
{
	float quantity_;
	float baseDamage_;
	float maxVarDamage_;

	UnitStack(int quantity)
	{
		this.quantity_ = quantity;
		this.baseDamage_ = 0;
		this.maxVarDamage_ = 0;
	}
	
	/**
	 * Merges in another stack of ships of the same type (not checked here). Damage among the stacks is kept.
	 */
	void mergeIn(UnitStack other)
	{
		baseDamage_ = (baseDamage_ * quantity_ + other.quantity_ * other.baseDamage_) / (quantity_ + other.quantity_); 
		maxVarDamage_ = (maxVarDamage_ * quantity_ + other.quantity_ * other.maxVarDamage_) / (quantity_ + other.quantity_);
		quantity_ += other.quantity_;
	}

	/**
	 * Changes the number of whole units in this stack. 
	 * @param delta A positive number if new ships should be added. If the number is positive, new units are assumed to be undamaged. If negative, the damage distribution is not altered.
	 */
	public void add(int delta)
	{
		// Check if the damage distribution needs recalculation...
		if(delta > 0)
		{
			baseDamage_ = (baseDamage_ * quantity_) / (quantity_ + delta); 
			maxVarDamage_ = (maxVarDamage_ * quantity_) / (quantity_ + delta);
		}
		quantity_ += delta;
	}
	
	/**
	 * @return the quantity
	 */
	public int quantity()
	{
		return (int) Math.ceil(quantity_);
	}

	/**
	 * @return the baseDamage
	 */
	public float baseDamage()
	{
		return baseDamage_;
	}

	/**
	 * @return the maxDamage
	 */
	public float maxVariableDamage()
	{
		return maxVarDamage_;
	}

}
