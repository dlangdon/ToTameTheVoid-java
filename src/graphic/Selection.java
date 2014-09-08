/**
 * 
 */
package graphic;

import java.util.ArrayList;

/**
 * TODO Encapsulates the selection in a single location, where it can be polled and queried by all the different classes that need to change it responding to different events.
 */
public class Selection
{
	public interface Observer
	{
		void selectionChanged(Object oldSelection, Object newSelection);
	}
	
	private static Object selected = null;
	private static ArrayList<Observer> observers = new ArrayList<Observer>();
	
	public static void register(Observer o)
	{
		observers.add(o);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSelectionAs(Class<?> as)
	{
		return (as.isInstance(selected) ? (T)selected : null);
	}
	
	public static boolean is(Object o)
	{
		return o == selected;
	}
	
	public static void set(Object o)
	{
		if(o == selected)
            return;

        Object old = selected;
		selected = o;
		System.out.println("selection changed: " + (old == null ? "null" : old.toString()) + " --> " + (o == null ? "null" : o.toString()));
		for(Observer obs: observers)
			obs.selectionChanged(old, o);
	}
	
	public static boolean exists()
	{
		return selected != null;
	}
}
