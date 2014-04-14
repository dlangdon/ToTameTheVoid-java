package ui;

import graphic.Images;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import ui.widget.Widget;

public class CornerMenu extends Widget
{
	EconomyDialog econ;

	CornerMenu() throws SlickException
	{
		this.setPosition(5, 5);
		econ = new EconomyDialog();
	}

	@Override
	public void render(GameContainer gc, Graphics g)
	{
		Images.CORNER_MENU.get().draw(x(),y());
		Images.ECONOMY_ICON.get().draw(x(),y());
		Images.CORNER_MENU.get().draw(x(),y());
		Images.CORNER_MENU.get().draw(x(),y());

		super.render(gc, g);
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		return super.screenCLick(x, y, button);
	}
}
