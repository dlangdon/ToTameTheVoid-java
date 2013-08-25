/**
 * 
 */
package event;

import military.Shipyard;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import state.Colony;
import state.Empire;
import state.HQ;
import state.IHQSlot;
import state.ImperialHQ;
import state.Star;
import state.UnitStack;

/**
 * Stores information when a star system can be colonized by an empire, allowing such an action.
 * @author Daniel Langdon
 */
public class ShipyardBuildEvent extends GameEvent
{
	Colony colony;
	int turns;
	int cost;
	
	/**
	 * Constructs a colonization event. 
	 */
	public ShipyardBuildEvent(Colony colony)
	{
		super(colony.location());
		this.colony = colony;
	}
	
	/**
	 * @return The icon to use if this action is going to be added to an interface.
	 */
	public Image icon()
	{
		// FIXME Ugly, I definitely need to tackle the whole resources thingy...
		try
		{
			return new Image("resources/icon_ihq.png");
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
		return 2;
	}

	/* (non-Javadoc)
	 * @see event.GameEvent#description()
	 */
	@Override
	public String description()
	{
		return "Build a shipyard in this colony";
	}
	
	@Override
	public void runAction()
	{
		// Check if an IHQ exist in this colony.
		HQ ihq = colony.ihq();
		if(ihq == null)
		{
			ihq = new Shipyard(colony);
			colony.setIhq(ihq);
		}
	}
}
