/**
 * 
 */
package military;

import org.newdawn.slick.Image;

import state.Unit;

/**
 * @author Daniel Langdon
 */
public class Ship extends Unit
{
	/**
	 * @param name_
	 * @param image_
	 */
	public Ship(String name_, Image image_)
	{
		super(name_, image_);
	}

	/* (non-Javadoc)
	 * @see state.Unit#type()
	 */
	@Override
	public String type()
	{
		return "Ship";
	}
}
