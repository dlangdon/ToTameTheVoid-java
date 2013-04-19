package main;
import java.util.List;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import state.Star;
import state.Universe;

public class Main extends StateBasedGame
{
	public enum GameStates
	{
		MAINGAME
	};

	public Main(String name)
	{
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException
	{
		this.addState(new Game());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			AppGameContainer app = new AppGameContainer(new Main("To tame the void"));
			app.setDisplayMode(800, 600, false);
			app.start();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}
}
