/**
 * 
 */
package ui.fonts;

import graphic.Render;
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
		g.setBackground(new Color(0xAA, 0x55, 0x55));

		// Paint configuration feedback
		g.setColor(Color.red);

//		AngelCodeFont font = Render.dialogSubTitle;
//		AngelCodeFont font = Render.dialogSubTitle;
		AngelCodeFont font = Render.dialogText;

		int y = 100;
		font.drawString(100, (y += font.getLineHeight()), "0123456789");
		font.drawString(100, (y += font.getLineHeight()), "ABCDEFGHIJ");
		font.drawString(100, (y += font.getLineHeight()), "KLMNOPQRST");
		font.drawString(100, (y += font.getLineHeight()), "UVWXYZ+=-*");
		font.drawString(100, (y += font.getLineHeight()), "abcdefghij");
		font.drawString(100, (y += font.getLineHeight()), "klmnopqrst");
		font.drawString(100, (y += font.getLineHeight()), "uvwxyz.,-?");

		font.drawString(100, (y += 50 + font.getLineHeight()), "Limit growth to 30% of normal development (plus notice).");
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
