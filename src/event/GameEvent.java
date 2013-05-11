/**
 * 
 */
package event;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import state.Star;

/**
 * Interface to create game events.
 * Events are handled by a main event loop, and are created and destroyed every turn.
 * Events can occur at a universal level (random events, victory conditions, etc), empire level (reserve runs low, etc) or location (space battle, etc).
 * In general, events may be informative-only or require player input. 
 * @author Daniel Langdon
 */
public abstract class GameEvent 
{
	/**
	 * Status of the event. Events start with pending status
	 */
	public enum Status { PENDING, RESOLVING, PARALLEL, DONE };
	
	private Status status_;
	private Star location_;
	
	GameEvent(Star location)
	{
		location_ = location;
	}
	
	public Star location()
	{
		return location_;
	}
	
	public Status status()
	{
		return status_;
	}

	public abstract void render(GameContainer gc, Graphics g) throws SlickException;
	public abstract void update(GameContainer gc, int delta) throws SlickException;
	
}
