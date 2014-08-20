/**
 *
 */
package com.github.dlangdon.ui.fonts;

import com.github.dlangdon.graphic.Render;
import org.newdawn.slick.*;

public class FontVisualTest extends BasicGame
{
	public FontVisualTest()
	{
		super("Font Test");
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	@Override
	public void init(GameContainer container) throws SlickException
	{
		Render.init();
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#update(org.newdawn.slick.GameContainer, int)
	 */
	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException
	{
		g.setAntiAlias(true);
		g.setBackground(Color.cyan);

		// Paint configuration feedback
		g.setColor(Color.red);

		AngelCodeFont font = Render.dialogSubTitle;

		font.drawString(100, 100, "0123456789");
		font.drawString(100, 150, "ABCDEFGHIJ");
		font.drawString(100, 200, "KLMNOPQRST");
		font.drawString(100, 250, "UVWXYZ");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			FontVisualTest test = new FontVisualTest();
			AppGameContainer app = new AppGameContainer(test);
			app.setDisplayMode(500, 500, false);
			app.start();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

}
