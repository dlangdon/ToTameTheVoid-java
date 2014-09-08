package simulation;

import java.util.*;

import empire.Empire;
import empire.View;
import simulation.checks.FleetMerger;
import galaxy.structure.MovementObserver;
import galaxy.structure.Place;
import galaxy.structure.Placeable;
import simulation.checks.InvasionCheck;
import simulation.checks.SpaceCombatCheck;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import simulation.options.Colonization;
import state.*;

/**
 * HIGHLY EXPERIMENTAL Event queue for special turn events. This is needed to
 * solve a special set of conflicts arising during the turn update that require
 * additional interaction, race conditions, information, etc. For instance:
 * - A fleet A arriving into a system might need to be merged to a fleet B already
 * in it. But we won't know if B will remain in the system until all fleets have
 * been updated.
 * - A fleet A arriving into a system might engage a fleet B
 * already in it, a separate module needs to be called to solve the conflict and one of them might be destroyed.
 * - An important event should go into a situation report or something similar.
 * @author Daniel Langdon
 */
public class Simulator
{
// Statics ==========================================================================================================	
	private static Simulator instance_;
	public static Simulator instance()
	{
		return instance_;
	}

// Internals ==========================================================================================================	
	private int turn;
	private Set<Star> startsToCheckForEvents;
	private Set<Lane> lanesToCheckForEvents;
	private HashMap<Star, List<Option>> options;

// Public methods ==========================================================================================================	

	/**
	 * Constructor.
	 */
	public Simulator()
	{
		instance_ = this;
		startsToCheckForEvents = new HashSet<>();
		lanesToCheckForEvents = new HashSet<>();
        options = new HashMap<>();

		turn = 0;

		Place.addObserver(new MovementObserver()
		{
            @Override
			public void arrivedAt(Placeable object, Place location)
			{
				if(location instanceof Star)
					startsToCheckForEvents.add((Star)location);
				else if(location instanceof Lane)
					lanesToCheckForEvents.add((Lane)location);
			}

			@Override
			public void departedAt(Placeable object, Place location)
			{
				arrivedAt(object, location);
			}
		});
	}

	public void addLaneToCheck(Lane l)
	{
		lanesToCheckForEvents.add(l);
	}

	/**
	 * Adds an option
	 */
	public void addOption(Option option)
	{
		List<Option> localEvents = options.get(option.location());
		if (localEvents == null)
		{
			localEvents = new ArrayList<Option>();
			options.put(option.location(), localEvents);
		}
		localEvents.add(option);
	}
	
	/**
	 * @param location
	 *           A location in the galaxy where events may occur.
	 * @return A list of all events tied to the specified location, which could
	 *         be empty. A copy of stored events is always returned, as actions on these events could modify this list by removing or adding items.
	 */
	public List<Option> optionsForLocation(Star location)
	{
		List<Option> localEvents = options.get(location);
		return localEvents == null ? new ArrayList<>() : new ArrayList<>(localEvents);
	}
	
	public void removeOption(Option event)
	{
		List<Option> localEvents = options.get(event.location());
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
		startsToCheckForEvents.clear();
		for (HQ hq : HQ.all())
			hq.turn();

		// Update all fleets
		for (Fleet f : Fleet.all())
			f.turn();

		// Update all empires.
		for (Empire e : Empire.all())
			e.getEconomy().applyGrowth(e.getColonies());

        // Check for new events.
		System.out.println("Checking events for turn " + turn);
		for (Star s : startsToCheckForEvents)
		{
            // Events with immediate resolution (no options generated)
			new FleetMerger().check(s);
			View.checkStarVisibility(s);
			new SpaceCombatCheck().check(s);    // Note that this happens after visibility, so even if destroyed, units get a glimpse.
			// TODO bombardments
            new InvasionCheck().check(s);       // TODO How do we ask the AI if it wants to invade? Assume always yes as in Moo? Might need to move this into an option instead?

            // Events that leave options, that can be executed later on.
            Colonization.check(s);
            // TODO Sabotages
		}

		// Update all lanes (has to be after stars, since we might need to check some lanes added manually when a star is no longer visible.
		for(Lane l : lanesToCheckForEvents)
		{
			View.checkLaneVisibility(l);
		}

		// TODO Try to process outstanding events.
		// TODO Update power snapshots for graphs and AI
	}

	public int getTurnCount()
	{
		return turn;
	}
}
