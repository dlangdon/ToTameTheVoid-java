package graphic;

import empire.Economy;
import empire.Empire;
import empire.View;
import galaxy.structure.Placeable;
import military.Shipyard;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import state.*;
import ui.*;
import simulation.Simulator;
import galaxy.generation.DelaunayLaneGenerator;
import galaxy.generation.EmpireInitialiazer;
import galaxy.generation.MinimumSpanningTreeForce;
import galaxy.generation.NascentGalaxy;
import galaxy.generation.RandomStarGenerator;
import galaxy.generation.SimpleBlobCreator;
import galaxy.generation.SimplePointCreator;
import galaxy.generation.StartingLocationFinder;
import ui.widget.Widget;

import java.util.Map;

import static graphic.Render.Visibility;

public class Game extends BasicGameState
{
	private Widget root;
	private FleetWidget fleetWidget;
	private Simulator eventQueue;
	private CornerMenu cornerMenu;

	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		// OpenGL initialization
		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		gc.setShowFPS(false);
		gc.setTargetFrameRate(120);

		Render.initialize();
		Images.initialize();

		// Run module initialization. Be careful with dependencies.
		// This is for now very hard-coded and not very modular.
		Economy.init();
		Fleet.init();
		Shipyard.init();

		// TODO figure out universe sizes, 500x500 for now. Size should be in the universe, not the camera!
		new Camera(new Vector2f(gc.getWidth(), gc.getHeight()), new Vector2f(600, 400), new Vector2f(350, 150));

		root = new Widget();
		cornerMenu = new CornerMenu(root);
		new StarWidget(root);
		fleetWidget = new FleetWidget(root);
		new HQWidget(root);

		eventQueue = new Simulator();

		// Initialize galaxy
		NascentGalaxy ng = new NascentGalaxy(600, 400, 4.0f);
//		ng.addForce(new StaticGalaxyCreator());
		ng.addForce(new SimpleBlobCreator(30, 4, 15), true);
		ng.addForce(new SimplePointCreator(5, 50), true);
		ng.addForce(new DelaunayLaneGenerator(0.15f), true);
		ng.addForce(new MinimumSpanningTreeForce(), true);
		ng.addForce(new StartingLocationFinder(5, 50), true);
		ng.addForce(new RandomStarGenerator(), true);
		ng.addForce(new EmpireInitialiazer(5), false);
		ng.blossom();

		// Pass two turns to reach a valid starting point (where last turn expenses are based on existing colonies).
		eventQueue.nextTurn();
		eventQueue.nextTurn();
	}

	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{
		// Draw backgrounds
		Images.BACKGROUND.get().draw(0, 0);
		g.setAntiAlias(false);  // This disables GL11.GL_POLYGON_SMOOTH, which prevents weird artifacts (diagonal lines) on PNG images.
		Camera.instance().pushWorldTransformation(g);

//		renderAll(gc, g);
		renderView(gc, g);

		// Draw in world widgets
		if(BaseDialog.current() instanceof IndexedDialog)
			BaseDialog.current().render(gc, g);

		// Draw HUD widgets
		g.popTransform();

		cornerMenu.render(gc, g);
		if(BaseDialog.current() instanceof MainDialog)
			BaseDialog.current().render(gc, g);

		// Draw events
		// eventQueue.render(gc, g);
	}

	private void renderAll(GameContainer gc, Graphics g)
	{
		// Draw Lanes
		Lane.renderAll(gc, g, Visibility.VISIBLE);

		// Draw Stars
		for(Star s : Star.all())
		{
			s.render(gc, g, Visibility.VISIBLE);
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
	}

	private void renderView(GameContainer gc, Graphics g)
	{
		View view = Empire.getPlayerEmpire().view();

		for(Map.Entry<Lane, Visibility> entry : view.getReachableLanes().entrySet())
		{
			entry.getKey().render(gc, g, entry.getValue());
			if(entry.getValue() == Visibility.VISIBLE)
			{
				for(Fleet f : entry.getKey().getFleets())
					f.render(gc, g);
			}
		}

		for(Map.Entry<Star, Visibility> entry : view.getRechableStars().entrySet())
		{
			entry.getKey().render(gc, g, entry.getValue());
			if(entry.getValue() == Visibility.VISIBLE)
			{
				for(Fleet f : entry.getKey().getFleets())
					f.render(gc, g);

				HQ hq = entry.getKey().getPlaceable(HQ.class);
				if(hq != null)
					hq.render(gc, g);
			}
		}
	}

	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
		// Process events and stop processing if modal.
		if(eventQueue.update(gc, delta))
			return;

		// Update the current widget
		Widget.underMouse().update(delta);

		// Update camera movement
		Camera.instance().update(delta);
	}

	/**
	 * Used for single clicks.
	 */
	@Override
	public void keyPressed(int key, char c)
	{
		if(cornerMenu.keyPressed(key))
			return;

		if(key == Input.KEY_PRIOR)
			Camera.instance().zoom(true, Camera.instance().getScreenCenter());
		else if(key == Input.KEY_NEXT)
			Camera.instance().zoom(false, Camera.instance().getScreenCenter());
		else if(key == Input.KEY_T && Keyboard.isKeyDown(Input.KEY_LCONTROL))
			eventQueue.nextTurn();
		else if(key == Input.KEY_SPACE)
			IndexedDialog.setDisabled(true);
		else if(key == Input.KEY_P)
		{
			int index = Empire.all().indexOf(Empire.getPlayerEmpire());
			Empire.setPlayerEmpire(Empire.all().get((index+1) % Empire.all().size()));
			IndexedDialog.setDisabled(true);
		}
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
		// If a widget is under the mouse, don't do anything, its update function will take care of it.
		if(Widget.underMouse() != Widget.NONE)
			return;

		UIListener newSelection = null;
		for(Map.Entry<Star, Visibility> entry : Empire.getPlayerEmpire().view().getRechableStars().entrySet())
		{
			Star s = entry.getKey();
			if(s.screenCLick((float)x, (float)y, button))
			{
				// Star was clicked.
				if(Selection.getSelectionAs(Fleet.class) != null)
                {
                    fleetWidget.starClick(button, s);
                    newSelection = Selection.getSelectionAs(UIListener.class);
                }
				// TODO HQ relocation
				else
					newSelection = s;
			}
			else
			{
				for(Placeable p: s.allPlaceables())
					if(p != null && p.screenCLick((float)x, (float)y, button))
						newSelection = p;
			}
		}
		Selection.set(newSelection);
	}

	@Override
	public void mouseWheelMoved(int change)
	{
		Camera.instance().zoom(change >= 0, new Vector2f(Mouse.getX(), Display.getHeight() - Mouse.getY()));
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy)
	{
		root.moveCursor(oldx, oldy, newx, newy);
	}

	@Override
	public int getID()
	{
		return Main.GameStates.MAINGAME.ordinal();
	}
}
