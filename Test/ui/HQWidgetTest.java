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
	
	public HQWidgetTest() throws SlickException
	{
		super("Generation Test");
	}
	
	@Override
	public void keyPressed(int key, char c)
	{
	}

	@Override
	public void mousePressed(int button, int x, int y)
	{
		// Check if any of the interfaces consumes this click.
		if(widget.screenCLick(x, y, button))
			return;

		if(hq.screenCLick((float)x, (float)y, button))
			widget.showHQ(hq);
		else
			widget.showHQ(null);
	}
	
	@Override
	public void mouseWheelMoved(int change)
	{
		Camera.instance().zoom(change >= 0, new Vector2f(Mouse.getX(), Display.getHeight() - Mouse.getY()));
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		widget.mouseMoved(oldx, oldy, newx, newy);
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
