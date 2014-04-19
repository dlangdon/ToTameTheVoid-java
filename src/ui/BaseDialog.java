package ui;

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

	public static void setCurrent(BaseDialog dialog, boolean toggle)
	{
		BaseDialog old = current;
		if(toggle && current == dialog)
			current = null;
		else
			current = dialog;

		if(current != old)
			for(EventListener<BaseDialog> l : listeners)
				l.onEvent(current);
	}
}
