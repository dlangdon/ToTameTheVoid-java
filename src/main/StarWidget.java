package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import state.Star;

public class StarWidget
{
// Internals ==========================================================================================================	
	Star star;
	Image background;
	Image meter;

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
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, star.getPos());

		// draw star icon
		g.setColor(Color.white);
		background.draw(-84, -119);
		g.drawString(star.name(), 100, -58);

		g.popTransform();
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
