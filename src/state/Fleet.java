package state;

import graphic.Camera;
import graphic.Render;
import graphic.UIListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Fleet implements UIListener, Comparable<Fleet>
{
	enum Type { SHIPS, AGENTS }
	
// Internals ==========================================================================================================
	private String name;
	private LinkedList<Star> destinations;		///< Route of stars to follow. The first star corresponds to the last star arrival.
	private int turnsTotal;							///< Number of turns that it takes to move to the next destination.
	private int turnsTraveled;               	///< Number of turns that have been moved towards that destination already.
	private float speed;								///< Minimum common speed for the stacks.
	private Empire owner_;							///< Empire that owns this Fleet.
	private Type type_;								///< Type of task fleet, to separate fleets from agents, etc.
	private TreeMap<Design, Integer> stacks;	///< Stacks composing this Fleet (individual ships and types).

// Public Methods =====================================================================================================
	Fleet( String name, Star orbiting, Empire empire, Type type )
	{
		// Base values
		this.speed = 10;
		this.owner_ = empire;
		this.type_ = type;
		this.turnsTraveled = 0;
		this.turnsTotal = 0;
		this.name = name;
		this.stacks = new TreeMap<Design, Integer>();

		this.destinations = new LinkedList<Star>();
		this.destinations.add(orbiting);
		orbiting.arrive(this);
	}

	/**
	 * Truncates the fleet route up to a specific star.
	 * @param destination Point at which the route is truncated. If the star was not part of the route, nothing happens.
	 * @return True if the route changed.
	 */
	public void removeFromRoute(Star destination)
	{
		// Find if destination is already included.
		int index = destinations.indexOf(destination);

		// Do not allow to remove edge while traveling it.
		if(index < 0 || index == 0 && turnsTraveled > 0)
			return;

		while(destinations.size() > index+1)
			destinations.removeLast();
	}

	/**
	 * @param destination Adds a new destination tt this fleet's queued route. 
	 */
	public void addToRoute(Star destination)
	{
		// Check if destination is reachable
		if(Lane.getDistance(destinations.getLast(), destination) > 0)
			destinations.addLast(destination);
		
		// Calculate route if this is the first destination.
		if(destinations.size() == 2)
			turnsTotal = (int) Math.ceil(Lane.getDistance(destinations.getFirst(), destinations.get(1)) / speed); 

	}

	/**
	 * Merges two fleets into a single one. Note that if the merge is successful, the fleet merged into this one is emptied, and should be later deleted.
	 * @param other Fleet to be merged with this. It must be of the same type and empire.
	 * @return true if the fleets were merged correctly and the other fleet removed.
	 */
	public boolean mergeIn(Fleet other)
	{
		// Check if merge is valid.
		if(owner_ != other.owner_ || type_ != other.type_)
			return false;
		
		// Do the merge.
		for(Entry<Design, Integer> entry : other.stacks.entrySet())
		{
			Integer quantity = stacks.get(entry.getKey());
			if(quantity == null)
				quantity = 0;
			
			stacks.put(entry.getKey(), entry.getValue() + quantity);
		}
		
		// Empty the other fleet.
		other.stacks.clear();
		return true;
	}
	
	public void addShips(Design kind, int number)
	{
		Integer current = stacks.get(kind);
		if(current == null)
			current = 0;
		stacks.put(kind, current + number);
	}

	public void turn()
	{
		// If no destinations, do nothing.
		if(destinations.size() < 2)
			return;

		// Check if we need to leave the current star.
		if(destinations.size() > 1 && turnsTraveled == 0)
			destinations.getFirst().leave(this);

		// Move the task fleet one turn forward.
		turnsTraveled++;
		
		// If we arrived at a star, put ourselves in orbit.
		if(turnsTraveled == turnsTotal)
		{
			turnsTraveled = 0;
			destinations.removeFirst();
			destinations.getFirst().arrive(this);
			
			if(destinations.size() > 1)
				turnsTotal = (int) Math.ceil(Lane.getDistance(destinations.getFirst(), destinations.get(1)) / speed); 
		}
	}	
	
	public void render(GameContainer gc, Graphics g, int flags)
	{
		Vector2f zero = new Vector2f();
		g.setColor(owner_.color());
		
		if((flags & Render.SELECTED) != 0)
		{
			g.setColor(Color.white);
			
			// Paint small dots for all our route, but only if the fleet is selected.
			Iterator<Star> i = destinations.iterator();
			Star to = i.next();
			Vector2f dir = new Vector2f();
			
			while(i.hasNext())
			{
				Star from = to;
				to = i.next();
				dir.set(to.getPos());
				dir.sub(from.getPos());
				
				int segments = (int) Math.ceil(Lane.getDistance(from, to) / speed);
				for(int s= (from == destinations.getFirst()) ? turnsTraveled : 1; s<segments; s++)
				{
					drawRoutePoint(dir.copy().scale(1.0f * s / segments).add(from.getPos()), g, zero);
				}
			}
		}
		
		// Paint the fleet icon.
		if(turnsTraveled == 0)
		{
			// Paint orbiting the star. In this case, each fleet is separated by a 30 degree angle.
			Vector2f pos = new Vector2f(20.0f, 0.0f);
			pos.setTheta(-30 * destinations.getFirst().getDock(this) - 30);
			drawIcon(destinations.getFirst().getPos(), g, pos);
		}
		else
		{
			// Paint on route.
			Vector2f dir = new Vector2f();
			dir.set(destinations.get(1).getPos());
			dir.sub(destinations.getFirst().getPos());
			drawIcon(dir.scale(1.0f * turnsTraveled / turnsTotal).add(destinations.getFirst().getPos()), g, zero);
		}
	}
	
	private void drawIcon(Vector2f world, Graphics g, Vector2f screenDisp)
	{
		Camera.instance().pushLocalTransformation(g, world);
		g.fillRect(screenDisp.x-4, screenDisp.y-4, 9, 9);
		g.popTransform();
	}

	private void drawRoutePoint(Vector2f world, Graphics g, Vector2f screenDisp)
	{
		Camera.instance().pushLocalTransformation(g, world);
		g.fillRect(screenDisp.x-2, screenDisp.y-2, 5, 5);
		g.popTransform();
	}
	
	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		Vector2f screen = new Vector2f(20.0f, 0.0f);
		if(turnsTraveled == 0)
		{
			// Force orbiting the star. In this case, each fleet is separated by a 30 degree angle.
			screen.setTheta(-30 * destinations.getFirst().getDock(this) - 30);
			screen.add(Camera.instance().worldToScreen(destinations.getFirst().getPos()));
		}
		else
		{
			// Click of a fleet in orbit. 
			screen = Camera.instance().worldToScreen(destinations.getFirst().getPos()); 
			Vector2f dir = Camera.instance().worldToScreen(destinations.get(1).getPos()).sub(screen);
			screen = dir.scale(1.0f * turnsTraveled / turnsTotal).add(screen); 
		}

		// Compare against mouse screen position.
		Vector2f local = new Vector2f(x, y).sub(screen);
		return (local.x * local.x <= 25 && local.y * local.y <= 25);
	}

	/**
	 * Task fleets are ordered in the following way:
	 * 	1.- A task fleet which orbits a colony of the same empire always goes first.
	 * 	2.- Task fleets are then ordered by empire, on a fixed order.
	 * 	3.- Task fleets are ordered by type.
	 * 
	 * Apart from these characteristics, task fleets are considered equivalent for ordering purposes, so this method is inconsistent with equals()
	 */
	@Override
	public int compareTo(Fleet o)
	{
		// Check if one or other is owner of the star.
		int aux = 0;
		Colony col = destinations.getFirst().getColony(); 
		if(col != null)
		{
			if(col.owner() == owner_)
				aux -= 1;
			if(col.owner() == o.owner_)
				aux += 1;
			
			if(aux != 0)
				return aux;
		}

		// Check if they belong to different empires.
		aux = this.owner_.name().compareTo(o.owner_.name());
		if(aux != 0)
			return aux;
		
		// Check their types.
		return type_.ordinal() - o.type_.ordinal();
	}

	/**
	 * @return The position were this fleet is located. If in orbit, this corresponds to the star's position, else, its coordinates in free space.
	 */
	public Vector2f position()
	{
		if(turnsTraveled == 0)
			return destinations.getFirst().getPos();
		
		Vector2f dir = new Vector2f();
		dir.set(destinations.get(1).getPos());
		dir.sub(destinations.getFirst().getPos());
		return dir.scale(1.0f * turnsTraveled / turnsTotal).add(destinations.getFirst().getPos());
	}
	
	/**
	 * @return A map corresponding to all stacks in this fleet. Each entry corresponds to a (Design, Integer) pair, corresponding to the number of chips of a particular design.
	 */
	public TreeMap<Design, Integer> stacks()
	{
		return stacks;
	}
	
	/**
	 * @return True if this fleet is empty (contains no stacks). Else false.
	 */
	public boolean isEmpty()
	{
		return stacks.size() == 0;
	}

	public Empire owner()
	{
		return owner_;
	}
	
	public Type type()
	{
		return type_;
	}
	
	public boolean hasOrders()
	{
		return destinations.size() > 1;
	}
}
