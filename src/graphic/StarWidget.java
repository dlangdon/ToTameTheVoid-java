package graphic;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import state.Colony;
import state.Star;

public class StarWidget
{
// Internals ==========================================================================================================	
	private Star star;
	private Image background;
	private Image meter;

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
		Colony colony = star.getColony(); 

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

			Render.normal.drawString(110, 36, "Production");
			Render.normal.drawString(110, 50, "Inv. return");
			Render.normal.drawString(210, 36, String.format("%2.2f", colony.production()*100.0));
			Render.normal.drawString(210, 50, String.format("%2.2f", colony.returnOfInvestment()));
		}
		else
		{
			Render.titles.drawString(100, 2, "No outpost");
		}
		
		g.popTransform();
	}
	
	private void drawMeter(Graphics g, float x, float y, float value)
	{
		meter.draw(x, y);
		g.fillRect(x+2, y+2, value*50.0f, 6);
	}
}
