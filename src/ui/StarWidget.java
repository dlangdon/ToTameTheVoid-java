package ui;

import empire.View;
import graphic.Camera;
import graphic.Render;
import graphic.Selection;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import simulation.Option;
import simulation.Simulator;
import state.Colony;
import state.Star;
import ui.widget.Widget;

import java.util.List;

public class StarWidget extends IndexedDialog
{
// Internals ==========================================================================================================	
	private Star star;
	private Image background;
	private Image meter;

// Public Methods =====================================================================================================
	public StarWidget(Widget parent) throws SlickException
	{
		super(parent);
		background = new Image("resources/starWidgetBck.png");
		meter = new Image("resources/meter.png");
		star = null;
		
		Selection.register((oldSelection, newSelection) -> {
			star = Selection.getSelectionAs(Star.class);
			if(star != null)
			{
				Camera.instance().ensureVisible(location(), 180, 370, 180, 180);
				IndexedDialog.setCurrent(StarWidget.this, false);
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
		g.setColor(Color.white);
		background.draw(-84, -119);

		switch (View.getVisibility(star))
		{
			case HIDDEN:
				return; // Should never happen.
			case REACHABLE:
				Render.dialogSubTitle.drawString(100, -88, "Unknown Star", Render.selectColor);
				Render.dialogText.drawString(120, -58, "Send a fleet to explore this location.", Render.baseColor);
				break;
			case REMEMBERED:
			case VISIBLE:
				Colony colony = star.getPlaceable(Colony.class);

				Render.dialogSubTitle.drawString(100, -88, star.name(), Render.selectColor);
				Render.dialogText.drawString(120, -58, "Resources", Render.baseColor);
				Render.dialogText.drawString(120, -44, "Conditions", Render.baseColor);
				Render.dialogText.drawString(120, -30, "Size", Render.baseColor);

				g.setColor(star.owner() == null ? Color.white : star.owner().color());
				drawMeter(g, 230, -58, star.resources());
				drawMeter(g, 230, -44, star.conditions());
				drawMeter(g, 230, -30, star.size());

				if(colony != null)
				{
					Render.dialogSubTitle.drawString(100, 2, star.owner().name(), Render.selectColor);

					Render.dialogText.drawString(120, 36, "Total Output", Render.baseColor);
					Render.dialogText.drawString(120, 50, "Inv. return", Render.baseColor);
					Render.dialogText.drawString(260, 36, String.format("%2.2f", (colony.production()-colony.maintenance())*10000.0), Render.baseColor);

					String invest = (Math.abs(colony.infrastructure() - colony.maxInfrastructure()) > 1E-6) ? String.format("%2.2f", colony.returnOfInvestment()) : "Maximum Reached";
					Render.dialogText.drawString(260, 50, invest, Render.baseColor);
				}
				else
				{
					Render.dialogSubTitle.drawString(100, 2, "No outpost", Render.selectColor);
				}

				// Render possible actions on this system.
				List<Option> existing = Simulator.instance().optionsForLocation(star);
				for(Option event : existing)
				{
					if(event.slot() >= 0)
					{
						Vector2f pos = indexToCoord(event.slot());
						event.icon().draw(pos.x-12, pos.y-12);

						// Check if we also display the local information.
						if(hoverIndex == event.slot())
							Render.dialogText.drawString(120, 80, event.description(), Render.baseColor);
					}
				}
				break;
		}
		g.popTransform();
	}
	
	private void drawMeter(Graphics g, float x, float y, float value)
	{
		meter.draw(x, y);
		g.fillRect(x+2, y+2, value*50.0f, 6);
	}

	public void mouseDown(int button, int delta)
	{
		// Check if visible.
		if(star == null || hoverIndex <= NO_INDEX || delta != 0 || disabled)
			return;
		
		// Process the corresponding action-event.
		for (Option option : Simulator.instance().optionsForLocation(star))
			if (option.slot() == hoverIndex)
				option.runAction();
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
