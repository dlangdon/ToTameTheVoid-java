/**
 * 
 */
package ui;

import empire.Economy;
import graphic.Camera;
import graphic.Render;
import graphic.Selection;
import military.Shipyard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import state.Star;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HQWidgetTest extends BasicGame
{
	HQWidget widget;
	Shipyard hq;
	private int mouseDownTime;
	
	public HQWidgetTest() throws SlickException
	{
		super("Generation Test");
		mouseDownTime = -1;
	}

	@Override
	public void mousePressed(int button, int x, int y)
	{
		mouseDownTime = 0;

		if(widget.isCursorInside())
			return;
		
		if(hq.screenCLick((float)x, (float)y, button))
			Selection.set(hq);
		else
			Selection.set(null);

		// Update the 
	}

	@Override
	public void	mouseReleased(int button, int x, int y)
	{
		mouseDownTime = -1;
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#update(org.newdawn.slick.GameContainer, int)
	 */
	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		if(mouseDownTime >= 0)
		{
			widget.mouseClick(Mouse.isButtonDown(0) ? 0 : 1);
			mouseDownTime += delta;
			System.out.println("mouseDownTime: " + mouseDownTime);
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		// Notify widgets.
		if(widget.moveCursor(oldx, oldy, newx, newy))
		{
			System.out.println("Mouse reset");
			mouseDownTime = -1;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	@Override
	public void init(GameContainer gc) throws SlickException
	{
		Star s = mock(Star.class);
		when(s.getPos()).thenReturn(new Vector2f(250, 250));
		
		widget = new HQWidget(null);
		hq = new Shipyard(s);
		
		Render.initialize();
		Economy.init();	// Needs to be initialized first.
		Shipyard.init();
		
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(500, 500), new Vector2f(0, 0));

	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.setBackground(Color.darkGray);
		widget.render(gc, g);
		hq.render(gc, g);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			HQWidgetTest test = new HQWidgetTest();
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
