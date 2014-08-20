/**
 *
 */
package com.github.dlangdon.simulation;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.github.dlangdon.state.Star;

/**
 * Interface to create game events.
 * Events are handled by a main event loop, and are created and destroyed every turn.
 * Events can occur at a universal level (random events, victory conditions, etc), com.github.dlangdon.empire level (reserve runs low, etc) or location (space battle, etc).
 * In general, events may be informative-only or require player input.
 *
 * @author Daniel Langdon
 */
public abstract class GameEvent implements Comparable<GameEvent>
{
	/**
	 * Status of the event. Events start with pending status.
	 */
	public enum Status { PENDING, RESOLVING, PARALLEL, DONE	};

	protected Status status_;
	private Star location_;

	protected GameEvent(Star location)
	{
		location_ = location;
		status_ = Status.PENDING;
	}

	public Star location()
	{
		return location_;
	}

	public Status status()
	{
		return status_;
	}

	public void render(GameContainer gc, Graphics g) throws SlickException
	{

	}

	public void update(GameContainer gc, int delta) throws SlickException
	{

	}

	/**
	 * A comparator to order game events. Events are ordered by location. null locations are considered less than any valid location.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GameEvent o)
	{
		if (o == this)
			return 0;
		if (location_ == null)
			return 1;
		else if (o.location_ == null)
			return -1;
		else
			return location().index() - o.location().index();
	}

	/**
	 * @return The icon to use if this action is going to be added to an interface.
	 */
	public Image icon()
	{
		return null;
	}

	/**
	 * @return An index that determines where this icon is put on the interface. The idea is that actions appear on the same location, independently of what is currently available.
	 */
	public int slot()
	{
		return 0;
	}

	public abstract String description();

	/**
	 * Performs whatever action is necessary.
	 */
	public void runAction()
	{
		// Default in case nothing has to be done.
	}
}
