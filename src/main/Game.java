package main;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import state.Lane;
import state.Star;
import state.Universe;

public class Game extends BasicGameState
{
	private Image background;
	private Image starfield;
	
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		// TODO figure out universe sizes, 500x500 for now.
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(500, 500));
		new Universe();

		// TODO load resources in a more intelligent way...
		background = new Image("resources/bck2.jpg");
		Star.img = new Image("resources/star.png");
		
		gc.setTargetFrameRate(120);
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{
		// Draw backgrounds
		background.draw(0, 0);
		
		// FIXME Only a test to check coordinate direction, although it looks kind of cool...
		g.setColor(Color.white);
		g.drawLine(5, 5, 5, 10);
		g.drawLine(5, 5, 10, 5);

		g.setAntiAlias(true);
		Camera.instance().pushWorldTransformation(g);

		// Draw Stars
		Lane.renderAll(gc, g);	// Lanes.
		
		for(Star s : Universe.instance().getStars())
		{
			s.render(gc, g);
		}
		
		// FIXME Temporary drawing world boundaries.
		Camera.instance().drawWorldLimits(g);

		g.popTransform();
	}

	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
		// Check for input
		Input input = gc.getInput();
		  
      // Window displacement
		Vector2f displacement = new Vector2f(0.0f, 0.0f);
		if(input.isKeyDown(Input.KEY_RIGHT))
      {
      	displacement.x = 1;
      }
		if(input.isKeyDown(Input.KEY_LEFT))
      {
      	displacement.x = -1;
      }
		if(input.isKeyDown(Input.KEY_UP))
      {
      	displacement.y = -1;
      }
		if(input.isKeyDown(Input.KEY_DOWN))
      {
      	displacement.y = 1;
      }
		
		// Update x and y positions.
		Camera.instance().move(displacement.scale(delta));
	}
	
	/**
	 * Used for single clicks.
	 */
	@Override
	public void keyPressed(int key, char c)
	{
		if(key == Input.KEY_PRIOR)
			Camera.instance().zoom(true, Camera.instance().getScreenCenter());
		if(key == Input.KEY_NEXT)
			Camera.instance().zoom(false, Camera.instance().getScreenCenter());
	}

	@Override
	public void mousePressed(int button, int x, int y)
	{
	}
	
	@Override
	public void mouseWheelMoved(int change)
	{
		Camera.instance().zoom(change >= 0, new Vector2f(Mouse.getX(), Display.getHeight() - Mouse.getY()));
	}
	
	@Override
	public int getID()
	{
		return Main.GameStates.MAINGAME.ordinal();
	}
}
