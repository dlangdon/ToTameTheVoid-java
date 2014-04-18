package ui;

import ui.widget.Widget;

/**
 * Basis for all dialogs, either indexed or main.
 * Guarantees that only one can be selected at any given time, and provides access to it.
 */
public abstract class BaseDialog extends Widget
{
	protected static BaseDialog current = null;

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
		if(toggle && current == dialog)
			current = null;
		else
			current = dialog;
	}
}
