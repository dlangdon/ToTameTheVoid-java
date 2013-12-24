package galaxy.structure;

/**
 * Created by Daniel on 12/22/13.
 */
public interface MovementObserver
{
	void arrivedAt(Placeable object, Place location);
	void departedAt(Placeable object, Place location);
}
