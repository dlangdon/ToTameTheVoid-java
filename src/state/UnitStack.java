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
	
	void add(UnitStack other)
	{
		baseDamage_ = (baseDamage_ * quantity_ + other.quantity_ * other.baseDamage_) / (quantity_ + other.quantity_); 
		maxVarDamage_ = (maxVarDamage_ * quantity_ + other.quantity_ * other.maxVarDamage_) / (quantity_ + other.quantity_);
		quantity_ += other.quantity_;
	}

	/**
	 * Changes the number of units in this stack.
	 * @param delta A positive number if new ships should be added.
	 * @param brandNew True if the added quantity is a positive number and the new ships are undamaged.
	 */
	public void add(int delta, boolean brandNew)
	{
		// Check if the damage distribution needs recalculation...
		if(brandNew && delta > 0)
		{
			baseDamage_ = (baseDamage_ * quantity_) / (quantity_ + quantity_); 
			maxVarDamage_ = (maxVarDamage_ * quantity_) / (quantity_ + quantity_);
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
