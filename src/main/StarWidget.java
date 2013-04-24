package main;

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
		
//	 	paintMeter(painter, 110, -38, color, "Resources", star.resources());
//	 	paintMeter(painter, 110, -24, color, "Conditions", star.conditions());
//	 	paintMeter(painter, 110, -10, color, "Size", star.size());

		
		g.popTransform();
	}
	
	private void drawMeter(Graphics g, float x, float y, float value)
	{
		meter.draw(x, y);
		g.drawRect(x+2, y+2, value*50.0f, 6);
		
		
		//	 	painter.setPen(Qt::white);
//	 	painter.drawText(x, y, text);
//	 	painter.setBrush(color);
//	 	painter.setPen(Qt::transparent);
//	 	painter.drawImage(x+100, y-11, *meter);
//	 	painter.drawRect(x+102, y-9, value*50, 6);
		
		// 		painter.drawText(110, 56, String("Production"));
// 		painter.drawText(110, 70, String("Inv. return"));
//
// 		painter.setFont(QFont("Arial", 10, QFont::Normal, true));
// 		painter.drawText(210, 56, String("%1 bc").arg(colony.production(), 0, 'f', 3));
// 		painter.drawText(210, 70, String("%1 turns").arg(colony.returnOfInvestment(), 0, 'f', 3));
		
	}

//	 
//	 /**
//	  *Size if 370x160 px.
//	  * @brief paintEvent
//	  * @param event
//	  */
//	 void paintEvent(QPaintEvent* event)
//	 {
//	 	// Recalculate positions due to possible movement.
//	 	QPointF pos = star.mapToScene(QPointF(0,0));
//	 	this.setGeometry(pos.x()-84, pos.y()-119, 443, 238);
//
//	 	QColor color = (star.colony() != 0 ) ? star.colony().owner().color() : Qt::white;
//	 	QPainter painter(this);
//	 	painter.drawImage(0, 0, *background);
//
//	 	// Basic painter configuration. Center painting on star.
//	 	painter.setRenderHint(QPainter::HighQualityAntialiasing);
//	 	painter.translate(84, 119);
//
//	 	painter.setPen(Qt::white);
//	 	painter.setFont(QFont("Arial", 12, QFont::Bold));
//	 	painter.drawText(100, -58, String("Star: %1").arg(star.name()));
//
//	 	painter.setFont(QFont("Arial", 10));
//	 	paintMeter(painter, 110, -38, color, "Resources", star.resources());
//	 	paintMeter(painter, 110, -24, color, "Conditions", star.conditions());
//	 	paintMeter(painter, 110, -10, color, "Size", star.size());
//
//	 	// Draw colony information, if any
//	 	Colony* colony = star.colony();
//	 	painter.setFont(QFont("Arial", 12, QFont::Bold));
//	 	painter.setPen(Qt::white);
//	 	if(colony != 0)
//	 	{
//	 		painter.drawText(100, 22, String("%1 Outpost").arg(colony.owner().name()));
//	 		painter.setFont(QFont("Arial", 10));
//	 		paintMeter(painter, 110, 42, color, "Infrastructure", colony.infrastructure()/colony.maxInfrastructure());
//
//	 		painter.setPen(Qt::white);
//	 		painter.drawText(110, 56, String("Production"));
//	 		painter.drawText(110, 70, String("Inv. return"));
//
//	 		painter.setFont(QFont("Arial", 10, QFont::Normal, true));
//	 		painter.drawText(210, 56, String("%1 bc").arg(colony.production(), 0, 'f', 3));
//	 		painter.drawText(210, 70, String("%1 turns").arg(colony.returnOfInvestment(), 0, 'f', 3));
//	 	}
//	 	else
//	 	{
//	 		painter.drawText(180, 118, "No outpost");
//	 	}
//
////	 	QColor minuteColor(0, 127, 127, 191);
////	     painter.setBackgroundMode();
////	 	painter.setRenderHint(QPainter::Antialiasing);
//
////	 	for (int j = 0; j < 60; ++j)
////	 	{
////	 		if ((j % 5) != 0)
////	 			painter.drawLine(92, 0, 96, 0);
////	 		painter.rotate(6.0);
//////	         painter.drawImage();
////	 	}
//	 }
//
//	 void paintMeter(QPainter painter, int x, int y,  QColor color,  String text, float value)
//	 {
//	 	painter.setPen(Qt::white);
//	 	painter.drawText(x, y, text);
//	 	painter.setBrush(color);
//	 	painter.setPen(Qt::transparent);
//	 	painter.drawImage(x+100, y-11, *meter);
//	 	painter.drawRect(x+102, y-9, value*50, 6);
//	 }
}
