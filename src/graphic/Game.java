package graphic;

import military.Shipyard;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import state.Economy;
import state.Fleet;
import state.HQ;
import state.Lane;
import state.Selection;
import state.Star;
import ui.EconomyDialog;
import ui.FleetWidget;
import ui.HQWidget;
import ui.IndexedDialog;
import ui.StarWidget;
import event.GameEventQueue;
import galaxy.generation.Galaxy;

public class Game extends BasicGameState
{
	private Image background;
	private StarWidget starWidget;
	private FleetWidget fleetWidget;
	private HQWidget hqWidget;
	private EconomyDialog econDialog;
	private GameEventQueue eventQueue;
	private int mouseDownTime;
	
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		// Run module initialization. Be careful with dependencies.
		// This is for now very hard-coded and not very modular.
		Economy.init();
		Fleet.init();
		Shipyard.init();
		
		// TODO figure out universe sizes, 500x500 for now. Size should be in the universe, not the camera!
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(600, 400), new Vector2f(350, 150));
		starWidget = new StarWidget();
		fleetWidget = new FleetWidget();
		hqWidget = new HQWidget();
		econDialog = new EconomyDialog();
		eventQueue = new GameEventQueue();
		new Galaxy();
		
		mouseDownTime = -1;
		
		// TODO load resources in a more intelligent way...
		Render.init();
		background = new Image("resources/bck1.jpg");
		Star.img = new Image("resources/star.png");
		
		gc.setTargetFrameRate(120);
		
		// Pass two turns to reach a valid starting point (where last turn expenses are based on existing colonies).
		eventQueue.nextTurn();
		eventQueue.nextTurn();
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{
		// Draw backgrounds
		background.draw(0, 0);
		g.setAntiAlias(true);
		Camera.instance().pushWorldTransformation(g);

		// Draw Lanes
		Lane.renderAll(gc, g);

		// Draw Stars
		for(Star s : Galaxy.instance().getStars())
		{
			s.render(gc, g);
		}

		// Draw fleets
		for(Fleet tf : Fleet.all())
		{
			tf.render(gc, g);
		}

		// Draw HQ
		for(HQ hq : HQ.all())
		{
			hq.render(gc, g);
		}
		
		// Draw in world widgets
//		if(currentDialog != null)
//			currentDialog.render(gc, g);
		starWidget.render(gc, g);
		fleetWidget.render(gc, g);
		hqWidget.render(gc, g);
		
		// Draw HUD widgets
		g.popTransform();
		econDialog.render(gc, g);
		
		// Draw events
		// eventQueue.render(gc, g);
	}

	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
		// Process events and stop processing if modal.
		if(eventQueue.update(gc, delta))
			return;

		// Some interfaces 
		if(mouseDownTime >= 0 && IndexedDialog.current() != null)
		{
			IndexedDialog.current().mouseClick(Mouse.isButtonDown(0) ? 0 : 1, mouseDownTime);
			mouseDownTime += delta;
		}
		
		Camera.instance().update(delta);
	}
	
	/**
	 * Advances the game to the next turn.
	 */
	public void turn()
	{
		// Move the universe forward.
		eventQueue.nextTurn();
	}
	
	/**
	 * Used for single clicks.
	 */
	@Override
	public void keyPressed(int key, char c)
	{
		if(key == Input.KEY_PRIOR)
			Camera.instance().zoom(true, Camera.instance().getScreenCenter());
		else if(key == Input.KEY_NEXT)
			Camera.instance().zoom(false, Camera.instance().getScreenCenter());
		else if(key == Input.KEY_T)
			this.turn();
		else if(key == Input.KEY_E)
			econDialog.setVisible(!econDialog.isVisible());
		else if(key == Input.KEY_SPACE)
			IndexedDialog.setDisabled(true);
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(key == Input.KEY_SPACE)
			IndexedDialog.setDisabled(false);
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{
		mouseDownTime = 0;
		if(IndexedDialog.current() != null && IndexedDialog.current().isCursorInside())
			return;

		// Check if the click corresponds to a star.
		Star selectedStar = null;
		for(Star s : Galaxy.instance().getStars())
		{
			if(s.screenCLick((float)x, (float)y, button))
			{
				selectedStar = s;
				break;
			}
		}

		// If a star, check if we add routepoints.
		if(selectedStar != null)
		{
			if(Selection.getSelectionAs(Fleet.class) != null)
			{
				fleetWidget.starClick(button, selectedStar);
				return;
			}
			else
			{
				Selection.set(selectedStar);
				IndexedDialog.setCurrent(starWidget);
			}
			
			// TODO HQ relocation
		}
		else
			Selection.set(null);

		// Check which objects may have received the click signal.
		for(Fleet tf : Fleet.all())
		{
			if(tf.screenCLick((float)x, (float)y, button))
			{
				Selection.set(tf);
				IndexedDialog.setCurrent(fleetWidget);
				break;
			}
		}
		for(HQ hq : HQ.all())
		{
			if(hq.screenCLick((float)x, (float)y, button))
			{
				Selection.set(hq);
				IndexedDialog.setCurrent(hqWidget);
				break;
			}
		}
	}
	
	@Override
	public void mouseReleased(int button, int x, int y)
	{
		mouseDownTime = -1;
	}
	
	@Override
	public void mouseWheelMoved(int change)
	{
		Camera.instance().zoom(change >= 0, new Vector2f(Mouse.getX(), Display.getHeight() - Mouse.getY()));
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		if(IndexedDialog.current() != null && IndexedDialog.current().moveCursor(oldx, oldy, newx, newy))
			mouseDownTime = -1;
	}
	
	@Override
	public int getID()
	{
		return Main.GameStates.MAINGAME.ordinal();
	}
}
