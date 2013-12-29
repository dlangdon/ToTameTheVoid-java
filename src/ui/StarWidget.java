package ui;

import simulation.GameEvent;
import simulation.Simulator;
import graphic.Camera;
import graphic.Render;
import graphic.Selection;
import graphic.Selection.Observer;

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

public class StarWidget extends IndexedDialog
{
// Internals ==========================================================================================================	
	private Star star;
	private Image background;
	private Image meter;

// Public Methods =====================================================================================================
	public StarWidget() throws SlickException
	{
		background = new Image("resources/starWidgetBck.png");
		meter = new Image("resources/meter.png");
		star = null;
		
		Selection.register(new Observer()
		{
			@Override
			public void selectionChanged(Object oldSelection, Object newSelection)
			{
				star = Selection.getSelectionAs(Star.class);
				if(star != null)
				{
					Camera.instance().ensureVisible(location(), 180, 370, 180, 180);
					IndexedDialog.setCurrent(StarWidget.this);
				}
			}
		});
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(star == null || disabled)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, star.getPos());

		Colony colony = star.getPlaceable(Colony.class);

		g.setColor(Color.white);
		background.draw(-84, -119);
		
		Render.titles.drawString(100, -78, star.name());

		Render.normal.drawString(110, -58, "Resources");
		Render.normal.drawString(110, -44, "Conditions");
		Render.normal.drawString(110, -30, "Size");
		
		g.setColor(star.owner() == null ? Color.white : star.owner().color());
		drawMeter(g, 210, -58, star.resources());
		drawMeter(g, 210, -44, star.conditions());
		drawMeter(g, 210, -30, star.size());

		if(colony != null)
		{
			Render.titles.drawString(100, 2, star.owner().name() + " outpost.");

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
		
		// Render possible actions on this system.
		List<GameEvent> existing = Simulator.instance().eventsForLocation(star);
		for(GameEvent event : existing)
		{
			if(event.slot() >= 0)
			{
				Vector2f pos = indexToCoord(event.slot());
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

	public void mouseClick(int button, int delta)
	{
		// Check if visible.
		if(star == null || hoverIndex <= NO_INDEX || delta != 0 || disabled)
			return;
		
		// Process the corresponding action-event.
		Iterator<GameEvent> existing = Simulator.instance().eventsForLocation(star).iterator();
		while(existing.hasNext())
		{
			GameEvent event = existing.next(); 
			if(event.slot() == hoverIndex)
				event.runAction();
		}
	}
	

	/* (non-Javadoc)
	 * @see ui.IndexedDialog#location()
	 */
	@Override
	public Vector2f location()
	{
		return star == null ? null : star.getPos();
	}
	

	/* (non-Javadoc)
	 * @see ui.IndexedDialog#indexToCoord(int)
	 */
	@Override
	protected Vector2f indexToCoord(int index)
	{
		float angle = index*30.0f + 15.0f;
		return new Vector2f(angle).scale(69.0f);
	}
	

	/* (non-Javadoc)
	 * @see ui.IndexedDialog#coordToIndex(org.newdawn.slick.geom.Vector2f)
	 */
	@Override
	protected int coordToIndex(Vector2f vector)
	{
		double angle = vector.getTheta();
		double radius = vector.length();
		
		if(54 < radius && radius < 84 )
			return (int)(angle/30.0f);

		return NO_INDEX;

	}
}
