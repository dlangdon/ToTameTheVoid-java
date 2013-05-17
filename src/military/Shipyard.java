/**
 * 
 */
package military;

import java.util.List;

import state.IHQSlot;
import state.Unit;

/**
 * A slot that builds military units.
 * @author Daniel Langdon
 */
public class Shipyard implements IHQSlot
{
	/* (non-Javadoc)
	 * @see state.IHQSlot#getType()
	 */
	@Override
	public String getType()
	{
		return "Shipyard";
	}

	/* (non-Javadoc)
	 * @see state.IHQSlot#inConstruction()
	 */
	@Override
	public Unit inConstruction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see state.IHQSlot#level()
	 */
	@Override
	public int level()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see state.IHQSlot#availableUnits()
	 */
	@Override
	public List<Unit> availableUnits()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
