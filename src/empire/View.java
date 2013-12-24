/**
 *
 */
package empire;

import galaxy.structure.Place;
import galaxy.structure.Placeable;
import graphic.Render.Visibility;
import simulation.GameEvent;
import simulation.LaneCheck;
import simulation.StarCheck;
import state.Lane;
import state.Star;

import java.util.HashMap;

/**
 * A view of the current state of the galaxy, as seen by a single empire.
 * This is a highly skewed view, as each empire will only have partial knowledge about what is going on, both due to fog of war and invisible units.
 * It is more efficient to update this view every time a turn begins, since we can distribute objects from the true state to each empire's view without having to go through it multiple times.
 * The scope of this class does not include understanding of tactical status (defended or undefended territory, frontier or core system, etc). The idea is to leave those concepts for the player or AI.
 * The scope is thus limited to rechableStars and accessibility (this given by empire relationships)
 *
 * For efficiency purposes, the visible state of all empires can be computed on a single run.
 * This is a good idea since even while there is a single player which view is being rendered, the AI will use this partial view to make decisions (we'll make it cheat in other ways hehe).
 * Also, for debugging/observing purposes we might want to switche the rendered view to what other empire is seeing.
 */
public class View
{
	private HashMap<Lane, Visibility> reachableLanes;
	private HashMap<Star, Visibility> rechableStars;

	View()
	{
		rechableStars = new HashMap<>();
		reachableLanes = new HashMap<>();
	}

	public HashMap<Star, Visibility> getRechableStars()
	{
		return rechableStars;
	}

	public HashMap<Lane, Visibility> getReachableLanes()
	{
		return reachableLanes;
	}

	public static Visibility getVisibility(Place p)
	{
		View v = Empire.getPlayerEmpire().view();
		Visibility aux = v.rechableStars.get(p);
		if(aux != null)
			return aux;
		return Visibility.HIDDEN;
	}

	public static StarCheck checkStarVisibility = new StarCheck()
	{
		@Override
		public GameEvent check(Star s)
		{
			// Remember if it was visible.
			for (Empire e : Empire.all())
				if(e.view().rechableStars.get(s) == Visibility.VISIBLE)
					e.view().rechableStars.put(s, Visibility.REMEMBERED);

			// Check if it is still visible.
			for(Placeable p: s.allPlaceables())
			{
				if(p.owner() == null)
					continue;

				// Visible if the empire has a placeable owned by him in this location.
				// TODO Extend to a list of allies.
				View view = p.owner().view();
				view.rechableStars.put(s, Visibility.VISIBLE);

				// Lanes from this star are also visible.
				for(Lane l: Lane.outgoingLanes(s))
				{
					view.reachableLanes.put(l, Visibility.VISIBLE);

					// This might discover some stuff.
					Star to = l.exitPoint(s);
					if(!view.rechableStars.containsKey(to))
						view.rechableStars.put(to, Visibility.REACHABLE);
				}
			}
			return null;
		}
	};

	public static LaneCheck checkLaneVisibility = new LaneCheck() {
		@Override
		public GameEvent check(Lane l)
		{
			// Remember if it was visible.
			for (Empire e : Empire.all())
				if(e.view().reachableLanes.get(l) == Visibility.VISIBLE)
					e.view().reachableLanes.put(l, Visibility.REMEMBERED);

			// Check if it is still visible.
			for(Placeable p: l.allPlaceables())
			{
				// Visible if the empire has a placeable owned by him in this location.
				// TODO Extend to a list of allies.
				if(p.owner() == null)
					p.owner().view().reachableLanes.put(l, Visibility.VISIBLE);
			}
			return null;
		}
	};
}
