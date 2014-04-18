package ui;

import graphic.Camera;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import ui.widget.Widget;

/**
 * Base abstract class for a dialog that indexes internal locations for interaction.
 * The index can be arbitrary.
 * @author Daniel Langdon
 */
public abstract class IndexedDialog extends BaseDialog
{
	protected static final int NO_INDEX = -10;
	protected static boolean disabled = false;

	/**
	 * Used to hide and disable the dialog.
	 * Useful to change visibility without affecting which dialog is selected.
	 */
	public static void setDisabled(boolean disabled)
	{
		IndexedDialog.disabled = disabled;
	}
	
	protected int hoverIndex;

	IndexedDialog(Widget parent)
	{
		super(parent);
		this.hoverIndex = NO_INDEX;
	}

	public IndexedDialog()
	{
		this(null);
	}
	
	/**
	 * This location can be mapped to the center of the dialog or to the upper-left corner, depending on the type of dialog (radial or square).
	 * @return The location where this widget is being displayed. null if no location has been set (dialog is hidden).
	 */
	public abstract Vector2f location();
	
	public abstract void render(GameContainer gc, Graphics g);
	
	/**
	 * Translates a placeholder index for a stack in this widget to a local coordinates.
	 * @param index Index to translate, must be in the range 0-22.
	 * @return A vector with the local coordinates, or null if not
	 */
	protected abstract Vector2f indexToCoord(int index);

	/**
	 * Finds the potential element in the UI that corresponds to a particular coordinate.
	 * This method does not consider what parts of the widget are current being displayed or their content, just placejolder locations.
	 * @param vector A 2-dimensional vector of screen coordinates centered on the current position.
	 * @return An index between 0 and 22 that corresponds to the slot in the widget that matches that point. OR a code between -1 to -5 to indicate one of the available buttons (top to bottom) OR NO_INDEX if there is no match whatsoever.
	 */
	protected abstract int coordToIndex(Vector2f vector);
	
	public boolean isCursorInside()
	{
		return !disabled && hoverIndex > NO_INDEX;
	}

	@Override
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		// Check children first
		for(Widget child : children)
			if(child.moveCursor(oldx, oldy, newx, newy))
				return true;

		// Check if we are active.
		int newHover = NO_INDEX;
		if(location() != null)
		{
			// Get the index to display.
			Vector2f local = new Vector2f(newx, newy).sub(Camera.instance().worldToScreen(location()));
			newHover = coordToIndex(local);
			System.out.printf("New index = %d\n", newHover);
		}

		if(newHover != hoverIndex)
		{
			hoverIndex = newHover;
			_milisMouseHover = 0;
		}

		if(newHover != NO_INDEX)
		{
			_underMouse = this;
			return true;
		}
		else if(_underMouse == this)
			_underMouse = NONE;
		return false;
	}
}
