/**
 *
 */
package com.github.dlangdon.simulation.actions;

import com.github.dlangdon.military.Shipyard;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.github.dlangdon.simulation.GameEvent;
import com.github.dlangdon.state.Star;

/**
 * Stores information when a star system can be colonized by an com.github.dlangdon.empire, allowing such an action.
 * @author Daniel Langdon
 */
public class ShipyardBuildEvent extends GameEvent
{
	int turns;
	int cost;

	/**
	 * Constructs a colonization event.
	 */
	public ShipyardBuildEvent(Star location)
	{
		super(location);
	}

	/**
	 * @return The icon to use if this action is going to be added to an interface.
	 */
	public Image icon()
	{
		// FIXME Ugly, I definitely need to tackle the whole resources thingy...
		try
		{
			return new Image("icon_ihq.png");
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
	 * @see com.github.dlangdon.simulation.GameEvent#description()
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
		Shipyard sy = location().getPlaceable(Shipyard.class);
		if(sy == null)
			sy = new Shipyard(location());
	}
}
