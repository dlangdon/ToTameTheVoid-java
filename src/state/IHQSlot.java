/**
 * 
 */
package state;

import java.util.List;

import org.newdawn.slick.Image;

/**
 * @author Daniel Langdon
 *
 */
public abstract class IHQSlot
{
	Unit inConstruction_;
	int level_;
	
	/**
	 * Unit that is currently being built.
	 */
	public Unit inConstruction()
	{
		return inConstruction_;
	}
	
	/**
	 * Level of this slot. The bigger the level, the more resources it can consume per turn.
	 */
	public int level()
	{
		return level_;
	}

	/**
	 * Type of slot, corresponding to the set of units it can produce.
	 */
	public abstract String getType();

	/**
	 * @return A list of all units that can be produced by this type of slot.
	 */
	public abstract List<Unit> availableUnits();

	/**
	 * @return A small (18x18) icon that corresponds to this slot.
	 */
	public abstract Image icon();
	
}
