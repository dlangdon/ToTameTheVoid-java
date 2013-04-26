package state;

import graphic.Camera;
import graphic.UIListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class TaskForce implements UIListener, Comparable<TaskForce>
{
	enum Type { SHIPS, AGENTS }
	
// Internals ==========================================================================================================
	private String name;
	private Star orbit;								///< Last visited star (or in orbit).
	private LinkedList<Star> to;             ///< Route of stars to follow.
	private int turnsTotal;						///< Number of turns that it takes to move to the next destination.
	private int turnsTraveled;               ///< Number of turns that have been moved towards that destination already.
	private float speed;                     ///< Minimun common speed for the stacks.
	private Empire owner;                    ///< Empire that owns this TaskForce.
	private Type type;

	Map<Design, Integer> stacks;									///< Stacks composing this TaskForce (individual ships and types).

// Public Methods =====================================================================================================
	TaskForce( String name, Star orbiting, Empire empire, Type type )
	{
		// Base values
		this.speed = 5;
		this.orbit = orbiting;
		this.owner = empire;
		this.type = type;
		this.turnsTraveled = 0;
		this.turnsTotal = 0;
		this.name = name;
		this.stacks = new HashMap<Design, Integer>();
		this.to = new LinkedList<Star>();
		
		orbit.arrive(this);
	}

	/// @return True if the route changed.
	boolean removeFromRoute(Star destination)
	{
		// Find if destination is already included.
		int index;
		if(destination == orbit)
			index = -1;
		else
		{
			index = to.indexOf(destination);
			if(index < 0)
				return false;
		}

		// Remove all the route from this destination onwards.
		for(int i=to.size()-1; i>index; i--)
			to.remove(i);
		return true;
	}

	/// @return True if the route changed.
	boolean addToRoute(Star destination)
	{
		// Find last departing point
		Star last = orbit;
		if(!to.isEmpty())
			last = to.get(to.size()-1);

		// Check if destination is reachable
		if(Lane.getDistance(last, destination) <= 0)
			return false;

		// Modify the route.
		to.add(destination);
		return true;
	}

	void addShips(Design kind, int number)
	{
		Integer current = stacks.get(kind);
		if(current != null)
			current = 0;
		stacks.put(kind, current + number);
	}

	void turn()
	{
		// If no destinations, do nothing.
		if(to.isEmpty())
			return;

		// Check if TaskForce is in orbit. In this case, detach from the orbiting star.
		if(orbit != null)
		{
			orbit.leave(this);
			orbit = null;
		}

		// Move the task force one turn forward.
		turnsTraveled++;
		
		// If we arrived at a star, put ourselves in orbit.
		if(turnsTraveled == turnsTotal)
		{
			turnsTraveled = 0;
			orbit = to.removeFirst();
			orbit.arrive(this);
			
			if(!to.isEmpty())
				turnsTotal = (int) Math.ceil(Lane.getDistance(orbit, to.getFirst()) / speed); 
		}
	}	
	
	public void render(GameContainer gc, Graphics g)
	{
		// Make it so drawing stars is always done in local coordinates.
		float scale = Camera.instance().scale(); 
		g.pushTransform();
//		g.scale(1.0f/scale, 1.0f/scale);
		g.setColor(owner.color());

		Vector2f pos = new Vector2f(20.0f, 0.0f);
		
		// Paint the fleet icon.
		if(turnsTraveled == 0)
		{
			// Paint orbiting the star. In this case, each taskforce is separated by a 30 degree angle.
			pos.setTheta(-30 * orbit.getDock(this) - 30);
			pos.add(orbit.getPos());

//			g.translate(x, y)
			g.fillRect(pos.x-5, pos.y-5, 10, 10);
		}
		else
		{
			// Paint on route.
			Vector2f dir = new Vector2f(to.getFirst().getPos()).sub(orbit.getPos()).normalise().scale(1.0f * turnsTraveled/turnsTotal);
			g.fillRect(dir.x-5, dir.y-5, 10, 10);
		}
		
		g.popTransform();
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		// TODO
		return false;
	}

	/**
	 * Task forces are ordered in the following way:
	 * 	1.- A task force which orbits a colony of the same empire always goes first.
	 * 	2.- Task forces are then ordered by empire, on a fixed order.
	 * 	3.- Task forces are ordered by type.
	 * 
	 * Apart from these characteristics, task forces are considered equivalent for ordering purposes, so this method is inconsistent with equals()
	 */
	@Override
	public int compareTo(TaskForce o)
	{
		// Check if one or other is owner of the star.
		int aux = 0;
		if(orbit.getColony() != null)
		{
			if(orbit.getColony().owner() == owner)
				aux += 1;
			if(orbit.getColony().owner() == o.owner)
				aux -= 1;
			
			if(aux != 0)
				return aux;
		}

		// Check if they belong to different empires.
		aux = this.owner.name().compareTo(o.owner.name());
		if(aux != 0)
			return aux;
		
		// Check their types.
		return type.ordinal() - o.type.ordinal();
	}
	
//	void paint(QPainter* painter,  QStyleOptionGraphicsItem*, QWidget*)
//	{
//		// Draw route to final destination.
//		painter.save();
//		if(true)//isSelected())
//		{
//			// Draw lines.
//			QPointF last = QPoint(0,0);
//			painter.setPen(QPen(Qt::red, 1, Qt::DashLine));
//			foreach(Star* s, to)
//			{
//				EchoDebug(String("Drawing from (%1,%2) to (%3,%4)").arg(last.x()).arg(last.y()).arg(s.pos().x()).arg(s.pos().y()));
//				painter.drawLine(last, s.pos() - pos());
//				last = s.pos() - pos();
//			}
//		}
//		painter.restore();
//
//		// Set star position and draw it.
//		painter.setBrush(owner.color());
//		painter.drawRect(-5, -5, 10, 10);
//		if(isSelected())
//			painter.drawRect(-7, -7, 14, 14);
//
//	}
//
//	QRectF boundingRect() 
//	{
//		return QRectF(-5, -5, 10, 10);
//	}

//	void mousePressEvent(QGraphicsSceneMouseEvent* event)
//	{
	// TODO
//		if(isSelected())
//		{
//			// When a star is clicked, the route needs to be changed for this TaskForce.
//			Star* star = dynamic_cast<Star*>(scene().itemAt(event.scenePos(), ));
//			if(star)
//			{
//			   if(event.button() == Qt::LeftButton)
//				   addToRoute(star);
//			   else
//				   removeFromRoute(star);
//			   event.accept();
//			}
//		}
//	}	
}
