/**
 * 
 */
package ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import graphic.Camera;
import graphic.Render;
import military.Shipyard;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.Economy;
import state.Star;

public class HQWidgetTest extends BasicGame
{
	HQWidget widget;
	Shipyard hq;

	private int mouseDownButton;
	private int mouseDownTime;
	
	public HQWidgetTest() throws SlickException
	{
		super("Generation Test");
		mouseDownButton = -1;
		mouseDownTime = 0;
	}

	@Override
	public void mousePressed(int button, int x, int y)
	{
		mouseDownButton = button;
		mouseDownTime = 0;

		if(widget.isCursorInside())
			return;
		
		if(hq.screenCLick((float)x, (float)y, button))
			widget.showHQ(hq);
		else
			widget.showHQ(null);

		// Update the 
	}

	@Override
	public void	mouseReleased(int button, int x, int y)
	{
		mouseDownButton = -1;
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#update(org.newdawn.slick.GameContainer, int)
	 */
	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		if(mouseDownButton >= 0)
		{
			widget.update(container, mouseDownTime);
			mouseDownTime += delta;
			System.out.println("mouseDownTime: " + mouseDownTime);
		}
	}
	
	@Override
	public void mouseWheelMoved(int change)
	{
		Camera.instance().zoom(change >= 0, new Vector2f(Mouse.getX(), Display.getHeight() - Mouse.getY()));
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		// Notify widgets.
		if(widget.hoverMove(oldx, oldy, newx, newy))
		{
			System.out.println("Mouse reset");
			mouseDownButton = -1;
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
		
		widget = new HQWidget();
		hq = new Shipyard(s);
		
		Render.init();
		Economy.init();	// Needs to be initialized first.
		Shipyard.init();
		
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(500, 500));

	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		g.setBackground(Color.darkGray);
		widget.render(gc, g);
		hq.render(gc, g, 0);
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
