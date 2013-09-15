package graphic;

import military.Shipyard;

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

import state.Economy;
import state.Fleet;
import state.HQ;
import state.Lane;
import state.Star;
import state.Universe;
import ui.EconomyDialog;
import ui.FleetWidget;
import ui.HQWidget;
import ui.StarWidget;
import event.GameEventQueue;

public class Game extends BasicGameState
{
	private Image background;
	private StarWidget starWidget;
	private FleetWidget fleetWidget;
	private HQWidget hqWidget;
	private EconomyDialog econDialog;
	private Object selected;
	private GameEventQueue eventQueue;
	
	private int mouseUpdateCount;
	private boolean showWorldMode;
	
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		// Run module initialization. Be careful with dependencies.
		// This is for now very hard-coded and not very modular.
		Economy.init();
		Fleet.init();
		Shipyard.init();
		
		// TODO figure out universe sizes, 500x500 for now. Size should be in the universe, not the camera!
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(500, 300));
		starWidget = new StarWidget();
		fleetWidget = new FleetWidget();
		hqWidget = new HQWidget();
		selected = null;
		econDialog = new EconomyDialog();
		eventQueue = new GameEventQueue();
		new Universe();
		
		showWorldMode = false;
		mouseUpdateCount = -1;
		
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
		
		// FIXME Only a test to check coordinate direction, although it looks kind of cool...
		g.setColor(Color.white);
		g.drawLine(5, 5, 5, 10);
		g.drawLine(5, 5, 10, 5);

		g.setAntiAlias(true);
		Camera.instance().pushWorldTransformation(g);

		// Draw Lanes
		Lane.renderAll(gc, g);

		// Draw Stars
		for(Star s : Universe.instance().getStars())
		{
			s.render(gc, g);
		}

		// Draw fleets
		for(Fleet tf : Universe.instance().getFleets())
		{
			tf.render(gc, g, (tf == selected) ? Render.SELECTED : 0);
		}

		// Draw HQ
		for(HQ hq : HQ.all())
		{
			hq.render(gc, g, (hq == selected) ? Render.SELECTED : 0);
		}
		
		// Draw in world widgets
		if(!showWorldMode)
		{
			starWidget.render(gc, g);
			fleetWidget.render(gc, g);
			hqWidget.render(gc, g);
		}
		
		// FIXME Temporary drawing world boundaries.
		Camera.instance().drawWorldLimits(g);

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

		// Check for input
		Input input = gc.getInput();

		// Some interfaces 
		if(!showWorldMode && mouseUpdateCount >= 0)
		{
			System.out.println("Mouse update: " + mouseUpdateCount);
			// Check if any of the interfaces consumes this click.
//			if(fleetWidget.screenCLick(Mouse.getX(), Mouse.getY(), button))
//				return;
//			if(hqWidget.screenCLick(mouseUpdateCount++))
//				return;
//			if(starWidget.screenCLick(Mouse.getX(), Mouse.getY(), button))
//				return;
		}
		
      // Window displacement
		Vector2f displacement = new Vector2f(0.0f, 0.0f);
		if(input.isKeyDown(Input.KEY_RIGHT) || Mouse.getX() > Display.getWidth() - 5)
      {
      	displacement.x = 1;
      }
		if(input.isKeyDown(Input.KEY_LEFT) || Mouse.getX() < 5)
      {
      	displacement.x = -1;
      }
		if(input.isKeyDown(Input.KEY_UP) || Mouse.getY() > Display.getHeight() - 5)
      {
      	displacement.y = -1;
      }
		if(input.isKeyDown(Input.KEY_DOWN) || Mouse.getY() < 5)
      {
      	displacement.y = 1;
      }
		
		// Update x and y positions.
		Camera.instance().move(displacement.scale(delta/10.0f));
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
			this.showWorldMode = true;
	}

	@Override
	public void keyReleased(int key, char c)
	{
		if(key == Input.KEY_SPACE)
			this.showWorldMode = false;
		
	}
	
	@Override
	public void mousePressed(int button, int x, int y)
	{
		mouseUpdateCount = 0;

		// Check if the click corresponds to a star.
		Star selectedStar = null;
		for(Star s : Universe.instance().getStars())
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
			Fleet fleet = fleetWidget.selectedfleet(); 
			if(fleet != null)
			{
				// If a fleet was selected and a star was clicked, we might be adding a route point.
				if(button == 0)
				{
					fleet = fleetWidget.getFleetFromSelection();
					fleet.addToRoute(selectedStar);
				}
				else
					fleet.removeFromRoute(selectedStar);
			}
			else
				selected = selectedStar;
			
			// TODO HQ relocation
		}
		else
			selected = null;
		
		// Check which objects may have received the click signal.
		for(Fleet tf : Universe.instance().getFleets())
		{
			if(tf.screenCLick((float)x, (float)y, button))
			{
				selected = tf;
				break;
			}
		}
		for(HQ hq : HQ.all())
		{
			if(hq.screenCLick((float)x, (float)y, button))
			{
				selected = hq;
				break;
			}
		}
		
		// Toggle modal interfaces according to the new selection.
		fleetWidget.showFleet((selected instanceof Fleet) ? (Fleet)selected : null);
		starWidget.showStar((selected instanceof Star) ? (Star)selected : null);
		hqWidget.showHQ((selected instanceof HQ) ? (HQ)selected : null);
	}
	
	@Override
	public void mouseReleased(int button, int x, int y)
	{
		mouseUpdateCount = -1;
		System.out.println("Mouse released: " + mouseUpdateCount);
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
		boolean reset = false;
		fleetWidget.mouseMoved(oldx, oldy, newx, newy);
		reset = reset || hqWidget.hoverMove(oldx, oldy, newx, newy);
		starWidget.mouseMoved(oldx, oldy, newx, newy);
		
		if(reset)
		{
			System.out.println("Mouse reset");
			mouseUpdateCount = 0;
		}
	}
	
	@Override
	public int getID()
	{
		return Main.GameStates.MAINGAME.ordinal();
	}
}
