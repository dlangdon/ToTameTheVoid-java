package ui;

public interface UIListener
{
	/**
	 * Handler method for click events in the screen layer.
	 * @param screen Screen coordinates for the click.
	 * @return True if the click was handled by this object, else false.
	 */
	boolean screenCLick(float x, float y, int button);
}
