package galaxy.structure;

import empire.Empire;
import state.Fleet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Place
{
	private static List<MovementObserver> observers = new ArrayList<>();
	public static void addObserver(MovementObserver observer) { observers.add(observer); }

// Internals ==========================================================================================================
	private List<Placeable> here;

// Public Methods =====================================================================================================
	public Place()
	{
		here = new LinkedList<>();
	}

	public void arrive(Placeable placeable)
	{
		// Find the correct location for this item and insert it there.
		int priority = placeable.priority();
		int index = 0;
		while(index < here.size() && priority <= here.get(index).priority())
			index++;
		here.add(index, placeable);

		for(MovementObserver o: observers)
			o.arrivedAt(placeable, this);
	}

	public void leave(Placeable placeable)
	{
		here.remove(placeable);
		for(MovementObserver o: observers)
			o.departedAt(placeable, this);
	}

	public int indexOf(Placeable selectable)
	{
		return here.indexOf(selectable);
	}

	public List<Fleet> getFleets()
	{
		List<Fleet> fleets = new ArrayList<Fleet>();
		for(Placeable o : here)
			if(o instanceof Fleet)
				fleets.add((Fleet) o);
		return fleets;
	}

	@SuppressWarnings("unchecked")
	public <T extends Placeable> T getPlaceable(Class<T> objectClass)
	{
		for(Placeable o : here)
			if(objectClass.isInstance(o))
				return (T)o;
		return null;
	}

    /**
     * Returns the empire that claims this location, or null if there is none.
     */
	public Empire owner()
	{
		return null;
	}

	public List<Placeable> allPlaceables()
	{
		return here;
	}
}
