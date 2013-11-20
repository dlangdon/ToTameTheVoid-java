package state;

/**
 * @author Daniel Langdon
 */
class PlaceView extends Place
{
	private Place truePlace_;
	private int lastUpdate_;
	
	
	
	/**
	 * For the purpose of comparisons, collections, etc, we would want the view to correspond to the actual true place.
	 * For instance, if we look for a placeview in a collection based on the true place, we should get it.
	 */
	@Override 
	public boolean equals(Object other)
	{
		return truePlace_.equals(other);
	}

	public int hashCode() 
	{
		return truePlace_.hashCode();
	}
}
