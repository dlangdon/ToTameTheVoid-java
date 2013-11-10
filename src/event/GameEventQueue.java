package event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import military.InvasionCheck;
import military.SpaceCombatCheck;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import state.Empire;
import state.Fleet;
import state.HQ;
import state.Star;

/**
 * HIGHLY EXPERIMENTAL Event queue for special turn events. This is needed to
 * solve a special set of conflicts arising during the turn update that require
 * additional interaction, race conditions, information, etc. For instance: - A
 * fleet A arriving into a system might need to be merged to a fleet B already
 * in it. But we won't know if B will remain in the system until all fleets have
 * been updated. - A fleet A arriving into a system might engage a fleet B
 * already in it, a separate module needs to be called to solve the conflict and one of them might be destroyed. -
 * An important event should go into a situation report or something similar.
 * 
 * @author Daniel Langdon
 */
public class GameEventQueue
{
// Statics ==========================================================================================================	
	private static GameEventQueue instance_;

	public static GameEventQueue instance()
	{
		return instance_;
	}

// Internals ==========================================================================================================	

	private int turn;
	private Set<Star> checkLocations;
	private HashMap<Star, List<GameEvent>> events;
	private LinkedList<TurnSubProcess> checks;

// Public methods ==========================================================================================================	

	/**
	 * Constructor.
	 */
	public GameEventQueue()
	{
		instance_ = this;
		checkLocations = new HashSet<Star>();
		events = new HashMap<Star, List<GameEvent>>();
		checks = new LinkedList<TurnSubProcess>();
		turn = 0;

		// FIXME Instantiate all sub processes. This should be done by configuration or something like that. Fixed for now.
//		registerSubProcess(new EconomyReset());			// Empire level
//		registerSubProcess(new FleetUpdater());			// Star
//		registerSubProcess(new ColonyUpdater());			// Star
		registerSubProcess(new FleetMerger()); // Star
		registerSubProcess(new SpaceCombatCheck()); // Star
		registerSubProcess(new InvasionCheck()); // Star
		registerSubProcess(new ColonizationCheck()); // Star
	}

	/**
	 * Registers a sub process to be evaluated at each turn. It is added at the
	 * end of the process queue. REGISTRATION ORDER IS EXTREMELLY IMPORTANT
	 * 
	 * @param subProcess
	 *           The process to add.
	 */
	public void registerSubProcess(TurnSubProcess subProcess)
	{
		checks.add(subProcess);
	}

	public void addLocationToCheck(Star location)
	{
		checkLocations.add(location);
	}

	/**
	 * Adds an event to the turn's event queue.
	 * 
	 * @param e
	 *           The event to add.
	 */
	public void addEvent(GameEvent event)
	{
		List<GameEvent> localEvents = events.get(event.location());
		if (localEvents == null)
		{
			localEvents = new ArrayList<GameEvent>();
			events.put(event.location(), localEvents);
		}
		localEvents.add(event);
	}
	
	/**
	 * @param location
	 *           A location in the galaxy where events may occur.
	 * @return A list of all events tied to the specified location, which could
	 *         be empty. A copy of stored events is always returned, as actions on these events could modify this list by removing or adding items.
	 */
	public List<GameEvent> eventsForLocation(Star location)
	{
		List<GameEvent> localEvents = events.get(location);
		return localEvents == null ? new ArrayList<GameEvent>() : new ArrayList<GameEvent>(localEvents);
	}
	
	public void removeEvent(GameEvent event)
	{
		List<GameEvent> localEvents = events.get(event.location());
		localEvents.remove(event);
	}

	/**
	 * Process a turn. This is done by running all turn sub-processes, which
	 * depend on individual modules. The order at which these processes are run
	 * is important, hence registration order matters.
	 */
	public void nextTurn()
	{
		// Jump the clock forward.
		turn++;
		System.out.println("New turn: " + turn);

		// Resets
		for (Empire e : Empire.all())
			e.getEconomy().resetTurn();

		// Produce new units
		checkLocations.clear();
		for (HQ hq : HQ.all())
			hq.turn();

		// Update all fleets
		for (Fleet f : Fleet.all())
			f.turn();

		// Update all empires.
		for (Empire e : Empire.all())
			e.getEconomy().applyGrowth(e.getColonies());

		// Update all stars
		System.out.println("Checking events for turn " + turn);
		for (Star s : checkLocations)
		{
			System.out.println("\tChecking location " + s.name());
			// Run all registered checks for this location.
			for (TurnSubProcess sp : checks)
				sp.check(this, s);
		}

		// Try to process outstanding events.
		
		// TODO Update power snapshots for graphs and AI

	}

	/**
	 * @param gc
	 * @param delta
	 * @return True if all input was processed on this event (effectively
	 *         creating a modal dialog for instance), else false.
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

	public int getTurnCount()
	{
		return turn;
	}
}
