package ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import ui.widget.Widget;

/**
 * Basis for all dialogs, either indexed or main.
 */
public abstract class BaseDialog extends Widget
{
	protected static BaseDialog current = null;

	public static BaseDialog current()
	{
		return current;
	}

	public static void setCurrent(BaseDialog dialog, boolean toggle)
	{
		if(toggle && current == dialog)
			current = null;
		else
			current = dialog;
	}

	public void mouseClick(int button, int delta)
	{
		// Default, do nothing.
	}

	/**
	 * @return True if the widget is visible and the cursor is inside it, as previously determined by moveCursor()
	 */
	public boolean isCursorInside()
	{
		return false;
	}

	/**
	 * Updates internal state of the cursor, used to determine hovering over elements, popups, etc.
	 * @return True if the mouse switched between two different elements by this movement, and any counter should be reset.
	 */
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		return false;
	}

	public void render(GameContainer gc, Graphics g) {}
}
