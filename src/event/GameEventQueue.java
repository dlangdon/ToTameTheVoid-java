package event;

import java.util.LinkedList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

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
	private int turn;
	private LinkedList<GameEvent> events;
	private LinkedList<TurnSubProcess> solvers;

	/**
	 * Constructor.
	 */
	public GameEventQueue()
	{
		events = new LinkedList<GameEvent>();
		solvers = new LinkedList<TurnSubProcess>();
		turn = 0;
		
		// FIXME Instantiate all sub processes. This should be done by configuration or something like that. Fixed for now.
		registerSubProcess(new EconomyReset());
		registerSubProcess(new FleetUpdater());
		registerSubProcess(new ColonyUpdater());
		registerSubProcess(new FleetMerger());
		registerSubProcess(new SpaceCombatCheck());
		registerSubProcess(new EmptyFleetRemover());
	}

	/**
	 * Registers a sub process to be evaluated at each turn. It is added at the end of the process queue.
	 * @param subProcess The process to add.
	 */
 	public void registerSubProcess(TurnSubProcess subProcess)
 	{
 		solvers.add(subProcess);
 	}
	
 	/**
 	 * Adds an event to the turn's event queue.
 	 * @param e The event to add.
 	 */
	public void addEvent(GameEvent e)
	{
		events.add(e);
	}
 	
	/**
	 * Process a turn.
	 * This is done by running all turn sub-processes, which depend on individual modules.
	 * The order at which these processes are run is important, hence registration order matters.
	 */
	public void nextTurn()
	{
		// Jump the clock forward.
		turn++;
		System.out.println("New turn: " + turn);

		// Run all update processes and conflict solvers.
		events.clear();
		for(TurnSubProcess sol : solvers)
		{
			sol.run(this);
		}
		
		// Try to process outstanding events.
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
		// TODO
//		Iterator<GameEvent> i = events.iterator();
//		for(GameEvent e: events)
//		{
//			e.update(gc, delta);
//
//			if(e.status() != Status.DONE)
//				return true;					// FIXME for now, all events are blocking (modal).
//			events.removeFirst();
//		}
		return false;
	}

}
