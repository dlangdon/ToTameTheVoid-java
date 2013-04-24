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
	Star star;
	Image background;
	Image meter;
	TrueTypeFont titles;
	TrueTypeFont normal;

// Public Methods =====================================================================================================
	StarWidget() throws SlickException
	{
		background = new Image("resources/starWidgetBck.png");
		meter = new Image("resources/meter.png");
		star = null;
		titles = new TrueTypeFont(new Font("Arial", Font.BOLD, 16), false);
		normal = new TrueTypeFont(new Font("Arial", Font.PLAIN, 12), false);
	}

	void showStar(Star star)
	{
		this.star = star;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, star.getPos());

		// draw star icon
		Colony colony = star.getColony(); 

		g.setColor(Color.white);
		background.draw(-84, -119);
		
		titles.drawString(100, -78, star.name());

		normal.drawString(110, -58, "Resources");
		normal.drawString(110, -44, "Conditions");
		normal.drawString(110, -30, "Size");
		
		g.setColor(colony == null ? Color.white : colony.owner().color());
		drawMeter(g, 210, -58, star.resources());
		drawMeter(g, 210, -44, star.conditions());
		drawMeter(g, 210, -30, star.size());

		if(colony != null)
		{
			titles.drawString(100, 2, colony.owner().name() + " outpost.");

			normal.drawString(110, 36, "Production");
			normal.drawString(110, 50, "Inv. return");
			normal.drawString(210, 36, String.format("%2.2f", colony.production()));
			normal.drawString(210, 50, String.format("%2.2f", colony.returnOfInvestment()));
		}
		else
		{
			titles.drawString(100, 2, "No outpost");
		}
		
		g.popTransform();
	}
	
	private void drawMeter(Graphics g, float x, float y, float value)
	{
		meter.draw(x, y);
		g.fillRect(x+2, y+2, value*50.0f, 6);
	}
}
