/**
 *
 */
package com.github.dlangdon.ai;

import com.github.dlangdon.empire.Empire;
import com.github.dlangdon.state.Fleet;
import com.github.dlangdon.state.Star;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A view of the current com.github.dlangdon.com.github.dlangdon.state of the com.github.dlangdon.galaxy, as seen by a single com.github.dlangdon.empire.
 * This is a highly skewed view, as each com.github.dlangdon.empire will only have partial knowledge about what is going on, both due to fog of war and invisible units.
 * It is more efficient to update this view every time a turn begins, since we can distribute objects from the true com.github.dlangdon.com.github.dlangdon.state to each com.github.dlangdon.empire's view without having to go through it multiple times.
 *
 * The scope of this class does not include understanding of tactical status (defended or undefended territory, frontier or core system, etc). The idea is to leave those concepts for the player or AI.
 * The scope is thus limited to visibility and accessibility (this given by com.github.dlangdon.empire relationships)
 *
 * @author Daniel Langdon
 */
public class PerceivedState
{
	public enum Classification { UNKNOWN, REMEMBERED, OWNED, UNCLAIMED, FRIENDLY, UNFRIENDLY }
	private static HashMap<Empire, PerceivedState> views;
	private HashMap<Object, Classification> classifications;

	/**
	 * Refresh the current view according to the current com.github.dlangdon.com.github.dlangdon.state, for all empires.
	 * Visibility rules are chosen such that agents have an important role, and to be as realistic as possible.
	 * Thus, a fleet can see:
	 * - The status of the star currently orbiting, if any.
	 * - The status of any fleet which has traveled at least one turn towards the current orbiting star.
	 * - The status of any fleet currently traversing the same lane.
	 *
	 * There are no remote sensor range or similar concept in place.
	 */
	public static void refreshAllViews()
	{
		views = new HashMap<Empire, PerceivedState>();
		for(Star s: Star.all())
		{
			// The star owner always gets sees it.
			if(s.owner() != null)
				setVisible(s.owner(), s);

			// Anyone in orbit does.
			for(Fleet f : s.getFleets())
				setVisible(f.owner(), s);
		}

		for(Fleet f: Fleet.all())
		{
			// If in orbit, nothing to do, it will be visible for anyone that sees the star itself.

			// The star owner always gets sees it.
		}

		// Mappings: com.github.dlangdon.empire --> grouping --> set of stars/fleets
		// Empire --> EmpireView --> hardcoded sets?

		// Determine:

		// Undefended stars in risk: those with a lane to territory not controlled by the same player or with a fleet
	}

	private static void setVisible(Empire e, Object o)
	{
		//
	}

	/**
	 * Returns the classification for the object, for a given com.github.dlangdon.empire.
	 * This interface emphasizes polling instead of optimized, cached data structures. The idea is to avoid premature optimization, since we don't know which collections will be traversed for AI and other computations.
	 */
	public static Classification getClassification(Empire e, Object o)
	{
		return views.get(e).classifications.get(o);
	}

	/**
	 * Returns all objects of type "as", classified as "c" for com.github.dlangdon.empire "e".
	 * For example, a list of all fleets friendly to the Meklars.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getAllAs(Empire e, Classification c, Class<?> as)
	{
		Set<T> result = new HashSet<T>();
		for(Entry<Object, Classification> entry : views.get(e).classifications.entrySet())
		{
			if(entry.getValue() == c && as.isInstance(entry.getKey()))
				result.add((T)entry.getKey());
		}
		return result;
	}
}
