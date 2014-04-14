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
}
