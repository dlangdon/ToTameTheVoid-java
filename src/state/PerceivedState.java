/**
 *
 */
package state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * A view of the current state of the galaxy, as seen by a single empire.
 * This is a highly skewed view, as each empire will only have partial knowledge about what is going on, both due to fog of war and invisible units.
 * It is more efficient to update this view every time a turn begins, since we can distribute objects from the true state to each empire's view without having to go through it multiple times.
 * The scope of this class does not include understanding of tactical status (defended or undefended territory, frontier or core system, etc). The idea is to leave those concepts for the player or AI.
 * The scope is thus limited to visibility and accessibility (this given by empire relationships)
 *
 * @author Daniel Langdon
 */
public class PerceivedState
{
	private static HashMap<Empire, PerceivedState> views = new HashMap<>();

	private Set<Star> visibleStars;
	private Set<Star> rememberedStars;
	private Set<Star> unknownStars;
	private Set<Lane> visibleLanes;
	private Set<Lane> rememberedLanes;

	PerceivedState(Empire e)
	{
		visibleStars = new HashSet<>();
		rememberedStars = new HashSet<>();
		unknownStars = new HashSet<>();
		visibleLanes = new HashSet<>();
		rememberedLanes = new HashSet<>();
		views.put(e, this);
	}

	public static PerceivedState getForEmpire(Empire e)
	{
		return views.get(e);
	}

	/**
	 * For efficiency purposes, the visible state of all empires can be computed on a single run.
	 * This is a good idea since even while there is a single player which view is being rendered, the AI will use this partial view to make decisions (we'll make it cheat in other ways hehe).
	 * Also, for debugging/observing purposes we might want to switche the rendered view to what other empire is seeing.
	 *
	 * FIXME: Put stars in only one collection, instead of replicating them, but this is easier and enough to try the concept.
	 */
	static public void refreshAllPerceptions()
	{
		// Clear and potentially instantiate all views.
		for (Empire e : Empire.all())
		{
			PerceivedState aux = views.get(e);
			if (aux == null)
				aux = new PerceivedState(e);
			aux.visibleLanes.clear();
			aux.visibleStars.clear();
		}

		// Traverse all stars
		for(Star s: Star.all())
		{
			for(Placeable p: s.allPlaceables())
			{
				PerceivedState view = views.get(p.owner());
				if(view != null)
				{
					view.visibleStars.add(s);
					view.rememberedStars.add(s);

					for(Lane l: Lane.outgoingLanes(s))
					{
						view.visibleLanes.add(l);
						view.rememberedLanes.add(l);

						Star to = l.exitPoint(s);
						view.unknownStars.add(to);
					}
					// TODO Add shared visibility for allied empires.
				}
			}
		}

		// Traverse all lanes
		for(Lane l: Lane.all())
		{
			for(Placeable p: l.allPlaceables())
			{
				PerceivedState view = views.get(p.owner());
				if(view != null)
				{
					view.visibleLanes.add(l);
					view.rememberedLanes.add(l);
					// TODO Add shared visibility for allied empires.
				}
			}
		}
	}

	public Set<Star> getVisibleStars() { return visibleStars; }

	public Set<Lane> getVisibleLanes() { return visibleLanes; }

	public Set<Star> getRememberedStars() { return rememberedStars; }

	public Set<Lane> getRememberedLanes() { return rememberedLanes; }

	public Set<Star> getUnknownStars() { return unknownStars; }
}
