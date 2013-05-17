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
		baseDamage_ = (maxVarDamage_ * quantity_ + other.quantity_ * other.maxVarDamage_) / (quantity_ + other.quantity_);
		quantity_ += other.quantity_;
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
