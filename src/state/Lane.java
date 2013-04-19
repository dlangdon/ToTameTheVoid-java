package state;

import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Class that encapsulates all lanes in the universe and all logic relative to pathfinding.
 * Note that drawing methods are static since lanes have no difference between each other (for now), also, centralization allows lanes to be drawn below other objects (i.e. stars) and only once.
 * @author Daniel Langdon
 */
public class Lane
{
// Internals ==========================================================================================================	
	private static HashMap<Integer, Lane> all = new HashMap<Integer, Lane>();
	
	private float length;
	private Star from;
	private Star to;
	
// Public Methods =====================================================================================================	
	public static float getDistance(Star from, Star to)
	{
		int key = createKey(from, to);
		Lane l = all.get(key);
		return l == null ? 0.0f : l.length;
	}
	
	public static List<Star> getPath(Star from, Star to)
	{
		// TODO
		return null;
	}

	public static void addLane(Star from, Star to)
	{
		Lane l = new Lane();
		l.length = (float) Math.sqrt((from.x() - to.x())*(from.x() - to.x()) + (from.y() - to.y())*(from.y() - to.y()));
		l.from = from;
		l.to = to;
		int key = createKey(from, to);
		System.out.format("\n\tAdded new lane from %s (%.1f, %.1f) to %s (%.1f, %.1f), key: %d", from.name(), from.x(), from.y(), to.name(), to.x(), to.y(), key);
		all.put(key, l);
	}
	
	public static void renderAll(GameContainer gc, Graphics g)
	{
		//g.setLineWidth(1);
		g.setColor(Color.cyan);
		for (Lane l : all.values())
		{
			g.drawLine(l.from.x(), l.from.y(), l.to.x(), l.to.y());
		}
	}

// Private Methods ====================================================================================================
	private static int createKey(Star from, Star to)
	{
		int a = from.index();
		int b = to.index();
		return ( a > b ? (a << 8) + b : (b << 8) + a );
	}

}
