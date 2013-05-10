package state;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import state.GameEvent.Status;

/**
 * HIGHLY EXPERIMENTAL
 * Event queue for special turn events. 
 * This is needed to solve a special set of conflicts arising during the turn update that require additional interaction, race conditions, information, etc.
 * For instance:
 *   - A fleet A arriving into a system might need to be merged to a fleet B already in it. But we won't know if B will remain in the system until all fleets have been updated.
 *   - A fleet A arriving into a system might engage a fleet B already in it, a separate module needs to be called to solve the conflict.
 *   - An important event should go into a situation report or something similar.
 * @author Daniel Langdon
 */
public class GameEventQueue
{
	private LinkedList<GameEvent> events;
	private HashSet<ConflictSolver> solvers;

	public GameEventQueue()
	{
		events = new LinkedList<GameEvent>();
		solvers = new HashSet<ConflictSolver>();
	}
	
 	public void registerSolver(ConflictSolver solver)
 	{
 		solvers.add(solver);
 	}
	
	public void addEvent(GameEvent e)
	{
		events.add(e);
	}
 	
	/**
	 * Goes over all locations to generate potential events.
	 */
	public void generateEvents()
	{
		events.clear();
		List<Star> locations = Universe.instance().getStars();
		for(Star s: locations)
		{
			for(ConflictSolver sol : solvers)
				sol.checkForEvents(s, this);
		}
	}
	
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		// TODO
	}
	
	/**
	 * 
	 * @param gc
	 * @param delta
	 * @return True if all input was processed on this event (effectively creating a modal dialog for instance), else false.
	 * @throws SlickException
	 */
	public boolean update(GameContainer gc, int delta) throws SlickException
	{
		while(!events.isEmpty())
		{
			GameEvent e = events.getFirst();
			e.update(gc, delta);

			if(e.status() != Status.DONE)
				return true;					// FIXME for now, all events are blocking (modal).
			events.removeFirst();
		}
		return false;
	}

}
