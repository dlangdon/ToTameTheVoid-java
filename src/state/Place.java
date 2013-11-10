package state;

import java.util.ArrayList;
import java.util.List;

import event.GameEventQueue;

public abstract class Place
{
// Internals ==========================================================================================================
	private List<Placeable> here;

// Public Methods =====================================================================================================
	public Place()
	{
		here = new ArrayList<Placeable>();
	}

	public void arrive(Placeable orbiter)
	{
		// Find the correct location for this item and insert it there.
		int priority = orbiter.priority();
		int index = 0;
		while(index < here.size() && priority <= here.get(index).priority())
			index++;
		here.add(index, orbiter);

		if(this instanceof Star)
			GameEventQueue.instance().addLocationToCheck((Star)this);
	}

	public void leave(Placeable orbiter)
	{
		here.remove(orbiter);
//		GameEventQueue.instance().addLocationToCheck(this);
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

	public Empire owner()
	{
		return null;
	}
}
