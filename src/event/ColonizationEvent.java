/**
 * 
 */
package event;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import state.Colony;
import state.Empire;
import state.Star;
import state.UnitStack;

/**
 * Stores information when a star system can be colonized by an empire, allowing such an action.
 * @author Daniel Langdon
 */
public class ColonizationEvent extends GameEvent
{
	UnitStack stack;
	Empire e;
	
	/**
	 * Constructs a colonization event. 
	 */
	public ColonizationEvent(Star location, UnitStack stack, Empire empire)
	{
		super(location);
		this.stack = stack;
		this.e = empire;
	}
	
	/**
	 * @return The icon to use if this action is going to be added to an interface.
	 */
	public Image icon()
	{
		try
		{
			return new Image("resources/icon_colonize.png");
		}
		catch (SlickException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @return An index that determines where this icon is put on the interface. The idea is that actions appear on the same location, independently of what is currently available.
	 */
	public int slot()
	{
		return 1;
	}

	/* (non-Javadoc)
	 * @see event.GameEvent#description()
	 */
	@Override
	public String description()
	{
		return "Colonize this star system.";
	}
	
	@Override
	public void runAction()
	{
		location().setColony(new Colony(location(), e));
	}
}
