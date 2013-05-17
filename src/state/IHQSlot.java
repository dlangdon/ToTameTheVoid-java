/**
 * 
 */
package state;

import java.util.List;

/**
 * @author Daniel Langdon
 *
 */
public interface IHQSlot
{
	/**
	 * Type of slot, corresponding to the set of units it can produce.
	 */
	public String getType();
	
	/**
	 * Unit that is currently being built.
	 */
	public Unit inConstruction();
	
	/**
	 * Level of this slot. The bigger the level, the more resources it can consume per turn.
	 */
	public int level();
	
	public List<Unit> availableUnits();
}
