package ui;

import graphic.Selection;
import ui.widget.EventListener;
import ui.widget.Widget;

import java.util.LinkedList;
import java.util.List;

/**
 * Basis for all dialogs, either indexed or main.
 * Guarantees that only one can be selected at any given time, and provides access to it.
 */
public abstract class BaseDialog extends Widget
{
	protected static BaseDialog current = null;
	private static List<EventListener<BaseDialog>> listeners = new LinkedList<>();
	public static void registerForSelectionChanges(EventListener<BaseDialog> listener)
	{
		listeners.add(listener);
	}

	BaseDialog(Widget parent)
	{
		super(parent);
	}

	public static BaseDialog current()
	{
		return current;
	}

	/**
	 * Sets the current base dialog.
	 * @param toggle If true and the same dialog was already selected, it is toggle to null instead.
	 */
	public static void setCurrent(BaseDialog dialog, boolean toggle)
	{
		BaseDialog old = current;
		if(toggle && current == dialog)
			current = null;
		else
			current = dialog;

		if(current != old)
		{
			// Selection of main dialogs erases object selection.
			if(current instanceof MainDialog)
				Selection.set(null);

			// If a widget appears or dissapears, what is under the mouse changes.
			Widget._underMouse = Widget.NONE;

			// Now notify all listeners.
			for(EventListener<BaseDialog> l : listeners)
				l.onEvent(current);
		}
	}

	@Override
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		return current == this && super.moveCursor(oldx, oldy, newx, newy);
	}
}
