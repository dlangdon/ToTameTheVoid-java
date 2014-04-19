package ui;

import graphic.Images;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import ui.widget.Widget;

/**
 * Corner menu who encapsulates the 3 main game dialogs.
 * It is not supposed to be a general menu widget, but a very specific one.
 */
public class CornerMenu extends Widget
{
	private EconomyDialog econDialog;
	private MainDialog techDialog;

	public CornerMenu(Widget parent) throws SlickException
	{
		super(parent);
		this.setPosition(0, 0);
		econDialog = new EconomyDialog(this);
		techDialog = new MainDialog(this);
	}

	@Override
	public void render(GameContainer gc, Graphics g)
	{
		Images.CORNER_MENU.get().draw(1, 1);
		Images.ECONOMY_ICON.get().draw(57,6);
		Images.SCIENCE_ICON.get().draw(40,38);
		Images.EMPIRES_ICON.get().draw(6,55);

		super.render(gc, g);
	}
}
