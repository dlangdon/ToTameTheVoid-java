package graphic;

import java.util.Iterator;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.Colony;
import state.Star;
import event.GameEvent;
import event.GameEvent.Status;
import event.GameEventQueue;

public class StarWidget implements UIListener
{
// Internals ==========================================================================================================	
	private Star star;
	private Image background;
	private Image meter;
	private int hoverIndex;

// Public Methods =====================================================================================================
	StarWidget() throws SlickException
	{
		background = new Image("resources/starWidgetBck.png");
		meter = new Image("resources/meter.png");
		star = null;
	}

	void showStar(Star star)
	{
		this.star = star;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(star == null)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, star.getPos());

		// draw star icon
		Colony colony = star.colony(); 

		g.setColor(Color.white);
		background.draw(-84, -119);
		
		Render.titles.drawString(100, -78, star.name());

		Render.normal.drawString(110, -58, "Resources");
		Render.normal.drawString(110, -44, "Conditions");
		Render.normal.drawString(110, -30, "Size");
		
		g.setColor(colony == null ? Color.white : colony.owner().color());
		drawMeter(g, 210, -58, star.resources());
		drawMeter(g, 210, -44, star.conditions());
		drawMeter(g, 210, -30, star.size());

		if(colony != null)
		{
			Render.titles.drawString(100, 2, colony.owner().name() + " outpost.");

			Render.normal.drawString(110, 36, "Total Output");
			Render.normal.drawString(110, 50, "Inv. return");
			Render.normal.drawString(210, 36, String.format("%2.2f", (colony.production()-colony.maintenance())*10000.0));
			
			if(Math.abs(colony.infrastructure() - colony.maxInfrastructure()) > 1E-6)
				Render.normal.drawString(210, 50, String.format("%2.2f", colony.returnOfInvestment()));
			else
				Render.normal.drawString(210, 50, "Maximum Reached");
		}
		else
		{
			Render.titles.drawString(100, 2, "No outpost");
		}
		
		// FIXME Coordinate test, remove later.
		for(int i=0; i<12; i++)
		{
			Vector2f pos = indexToCenterCoord(i);
			g.drawRect(pos.x-1, pos.y-1, 3, 3);
			Render.normal.drawString(pos.x, pos.y+3, Integer.toString(i));
		}
		
		// Render possible actions on this system.
		List<GameEvent> existing = GameEventQueue.instance().eventsForLocation(star);
		for(GameEvent event : existing)
		{
			if(event.slot() >= 0)
			{
				Vector2f pos = indexToCenterCoord(event.slot());
				event.icon().draw(pos.x-12, pos.y-12);

				// Check if we also display the local information.
				if(hoverIndex == event.slot())
					Render.titles.drawString(120, -100, event.description());
			}
		}
		
		g.popTransform();
	}
	
	private void drawMeter(Graphics g, float x, float y, float value)
	{
		meter.draw(x, y);
		g.fillRect(x+2, y+2, value*50.0f, 6);
	}

	/**
	 * Translates a placeholder index for a stack in this widget to a local coordinates (around widget's center).
	 * @param index Index to translate, must be in the range 0-11.
	 * @return A vector with the local coordinates
	 */
	private Vector2f indexToCenterCoord(int index)
	{
		float angle = index*30.0f + 15.0f;
		return new Vector2f(angle).scale(69.0f);
	}
	
	/**
	 * Finds the potential element in the UI that corresponds to a particular coordinate.
	 * This method does not consider what parts of the widget are current being displayed or their content, just placeholder locations.
	 * @param vector A 2-dimensional vector of screen coordinates centered on the current position.
	 * @return An index between 0 and 11 that corresponds to the slot in the widget that matches that point, -1 if the vector does not match any placeholder.
	 */
	private int coordToIndex(Vector2f vector)
	{
		double angle = vector.getTheta();
		double radius = vector.length();
		
		if(54 < radius && radius < 84 )
			return (int)(angle/30.0f);

		return -1;
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		// Check if visible.
		if(star == null)
			return false;
		
		// Get the index.
		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(star.getPos()));
		int index = coordToIndex(local);
		if(index < 0)
			return false;
		
		// Process the corresponding action-event.
		Iterator<GameEvent> existing = GameEventQueue.instance().eventsForLocation(star).iterator();
		while(existing.hasNext())
		{
			GameEvent event = existing.next(); 
			if(event.slot() == index)
				event.runAction();
		}
		return true;
	}
	
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		// Check if we are active.
		if(star == null)
			return;
		
		// Get the index to display.
		Vector2f local = new Vector2f(newx, newy).sub(Camera.instance().worldToScreen(star.getPos()));
		hoverIndex = coordToIndex(local);
	}
}
