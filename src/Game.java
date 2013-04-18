
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import state.Star;
import state.Universe;

public class Game extends BasicGameState
{
	private Image background;
	private Image starfield;

	private int viewX;
	private int viewY;
	
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		new Universe();
		background = new Image("resources/bck2.jpg");
		gc.setTargetFrameRate(120);
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{
		// Draw backgrounds
		background.draw(-viewX, -viewY);
		
		
		// Draw Stars
		g.translate(-viewX, -viewY);
		
		// TODO This is weird...
		for(Star s : Universe.instance().getStars())
		{
			s.render(gc, g);
		}
	}

	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
		// Check for input
		Input input = gc.getInput();
		System.out.println("\nUpdate: " + delta + "ms.");
		  
      // Window displacement
		int displacementX = 0;
		int displacementY = 0;
		if(input.isKeyDown(Input.KEY_RIGHT))
      {
      	displacementX = 1;
      }
		if(input.isKeyDown(Input.KEY_LEFT))
      {
      	displacementX = -1;
      }
		if(input.isKeyDown(Input.KEY_UP))
      {
      	displacementY = -1;
      }
		if(input.isKeyDown(Input.KEY_DOWN))
      {
      	displacementY = 1;
      }
		
		// Update x and y positions.
		viewX += (int) displacementX * delta;
		viewY += (int) displacementY * delta;
	}

	@Override
	public int getID()
	{
		return Main.GameStates.MAINGAME.ordinal();
	}
}
