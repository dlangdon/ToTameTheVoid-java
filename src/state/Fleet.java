package state;

import galaxy.generation.Galaxy;
import graphic.Camera;
import graphic.Render;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Fleet extends Orbiter
{
// Statics ============================================================================================================
	private static int maintenanceExpense;
	private static Image fleet;
	
	/**
	 * Global initialization phase to produce module constants, registration with other modules, resource loading, etc.
	 */
	public static void init() throws SlickException
	{
		maintenanceExpense = Economy.registerCause("Fleet Maintenance");
		fleet = new Image("resources/fleet.png");
	}
	
// Internals ==========================================================================================================
	private LinkedList<Star> destinations;		///< Route of stars to follow. The first star corresponds to the last star arrival.
	private int turnsTotal;							///< Number of turns that it takes to move to the next destination.
	private int turnsTraveled;               	///< Number of turns that have been moved towards that destination already.
	private float speed;								///< Minimum common speed for the stacks.
	private Empire owner_;							///< Empire that owns this Fleet.
	private TreeMap<Unit, UnitStack> stacks;	///< Stacks composing this Fleet (individual ships and types).

// Public Methods =====================================================================================================
	
	/**
	 * @param orbiting Star this fleet is going to be orbiting.
	 * @param empire
	 */
	public Fleet(Star orbiting, Empire empire)
	{
		super(orbiting);

		// Base values
		this.speed = 10;
		this.owner_ = empire;
		this.turnsTraveled = 0;
		this.turnsTotal = 0;
		this.stacks = new TreeMap<Unit, UnitStack>();
		this.destinations = new LinkedList<Star>();
		this.destinations.add(orbiting);

		orbiting.arrive(this);	// Needs to happen after destinations exist, else priority can't be calculated.
		Galaxy.instance().getFleets().add(this);
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
	 * @param destination Adds a new destination to this fleet's queued route. 
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
	 * @return the planned route for this fleet.
	 */
	public List<Star> getRoute()
	{
		return destinations;
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
	 * @return True if this fleet has orders to go somewhere, else false.
	 */
	public boolean hasOrders()
	{
		return destinations.size() > 1;
	}
	
	/**
	 * Merges two fleets into a single one. Note that if the merge is successful, the fleet merged into this one is emptied, and should be later deleted.
	 * @param other Fleet to be merged with this. It must be of the same type and empire.
	 * @return true if the fleets were merged correctly and the other fleet removed.
	 */
	public boolean mergeIn(Fleet other)
	{
		// Check if merge is valid.
		if(owner_ != other.owner_ || type() != other.type())
			return false;
		
		// Do the merge.
		for(Entry<Unit, UnitStack> entry : other.stacks.entrySet())
		{
			// Find a stack in this fleet for the other's stack design. Add one if there is none.
			UnitStack local = stacks.get(entry.getKey());
			if(local == null)
			{
				local = new UnitStack(0);
				stacks.put(entry.getKey(), local);
			}
			
			// Update the local stack.
			local.add(entry.getValue());
		}
		
		// Empty the other fleet.
		other.stacks.clear();
		return true;
	}
	
	/**
	 * Splits this fleet into 2 fleets, maintaining other info.
	 * @param units A map indicating how many units and from which stacks they should be taken. Specified units are expected to be valid (a valid Unit type, plus enough units to remove). An empty map will create an empty copy of this fleet.
	 * @return A new fleet with the specified stacks and units and no route to follow, sharing all other stats (owner, etc) with the current fleet.
	 */
	public Fleet split(Map<Unit, Integer> units)
	{
		Fleet aux = new Fleet(destinations.getFirst(), owner_);
		
		// Create new stacks.
		for(Entry<Unit, Integer> split : units.entrySet())
		{
			// Remove from old stack.
			UnitStack previous = stacks.get(split.getKey());
			previous.quantity_ -= split.getValue();
			if(previous.quantity_ <= 0)
				stacks.remove(split.getKey());

			// Create new stack.
			UnitStack created = new UnitStack(split.getValue());
			created.baseDamage_ = previous.baseDamage_;
			created.maxVarDamage_ = previous.maxVarDamage_;
			aux.stacks.put(split.getKey(), created);
		}
		
		return aux;
	}
	
	public void addUnits(Unit kind, int number)
	{
		// FIXME fix for subtraction
		if(number != 0)
		{
			UnitStack toAdd = new UnitStack(number);
			UnitStack current = stacks.get(kind);
			if(current == null)
				stacks.put(kind, toAdd);
			else
				current.add(toAdd);
		}
	}

	/**
	 * Applies a certain amount of damage to the stack of ships specified by the design.
	 * @param kind Stack of ships to apply the damage.
	 * @param damage Amount of damage to apply.
	 */
	public void takeDamage(Unit kind, float damagePerHit, int numHits, boolean unfocused)
	{
		UnitStack stack = stacks.get(kind);
		if(stack == null)
			return;

		// Update the damage stats.
		if(unfocused)																						// Case 1: unfocused attack
			stack.baseDamage_ += damagePerHit * numHits / stack.quantity_;
		else if(damagePerHit > kind.hitPoints())													// Case 2: kill ships directly.
			stack.quantity_ -= numHits;
		else																									// Case 3: distribute damage around.
			stack.maxVarDamage_ +=  2.0f * damagePerHit * numHits / stack.quantity_;
		
		// Check how many units are left, by intersecting the damage curve: y = b + m/q * x and hitpoints: y = max_hp. Careful with zero div.
		if(stack.maxVarDamage_ <= 0.0f)
			stack.quantity_ =  (stack.baseDamage_ >= kind.hitPoints()) ? 0 : stack.quantity_;
		else
			stack.quantity_ = (kind.hitPoints() - stack.baseDamage_) * stack.quantity_ / stack.maxVarDamage_;
		
		// Remove the stack if it is killed completely.
		if(stack.quantity_ <= 0)
			stacks.remove(kind);
	}
	
	public void turn()
	{
		// Generate expenses (repair and maintenance) for this turn.
		float expenses = 0.0f;
		for(Entry<Unit, UnitStack> entry : stacks.entrySet())
		{
			// Expenses are 1% of original ship cost per turn. After 100 turns they become a liability ;-)
			expenses -= entry.getKey().cost() * entry.getValue().quantity() * 0.01f;
			// TODO Repairs
		}
		System.out.println("Total fleet maintenance:" + expenses);
		owner_.getEconomy().addMovement(expenses, maintenanceExpense);

		// Go for movements. If no destinations, do nothing.
		if(destinations.size() < 2)
			return;

		// Check if we need to leave the current star.
		if(destinations.size() > 1 && turnsTraveled == 0)
		{
			destinations.getFirst().leave(this);
			location_ = null;
		}

		// Move the task fleet one turn forward.
		turnsTraveled++;
		
		// If we arrived at a star, put ourselves in orbit.
		if(turnsTraveled == turnsTotal)
		{
			turnsTraveled = 0;
			destinations.removeFirst();
			destinations.getFirst().arrive(this);
			location_ = destinations.getFirst();
			
			if(destinations.size() > 1)
				turnsTotal = (int) Math.ceil(Lane.getDistance(destinations.getFirst(), destinations.get(1)) / speed); 
		}
	}	
	
	@Override
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
			drawIcon(destinations.getFirst().getPos(), g, pos, owner_.color());
		}
		else
		{
			// Paint on route.
			Vector2f dir = new Vector2f();
			dir.set(destinations.get(1).getPos());
			dir.sub(destinations.getFirst().getPos());
			drawIcon(dir.scale(1.0f * turnsTraveled / turnsTotal).add(destinations.getFirst().getPos()), g, zero, owner_.color());
		}
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
		if(turnsTraveled == 0)
			return super.screenCLick(x, y, button);

		Vector2f screen = Camera.instance().worldToScreen(destinations.getFirst().getPos()); 
		Vector2f dir = Camera.instance().worldToScreen(destinations.get(1).getPos()).sub(screen);
		screen = dir.scale(1.0f * turnsTraveled / turnsTotal).add(screen); 

		// Compare against mouse screen position.
		Vector2f local = new Vector2f(x, y).sub(screen);
		return (local.x * local.x <= 25 && local.y * local.y <= 25);
	}
	
	/**
	 * @return A map corresponding to all stacks in this fleet. Each entry corresponds to a (Design, Integer) pair, corresponding to the number of chips of a particular design.
	 */
	public TreeMap<Unit, UnitStack> stacks()
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

	/**
	 * Type of units in this fleet. All units have to be of the same type.
	 * @return
	 */
	public String type()
	{
		if(stacks.isEmpty())
			return "None";
		return stacks.firstKey().type();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Fleet:");
		for(Entry<Unit, UnitStack> entry : stacks.entrySet())
		{
			sb
				.append("\n\t")
				.append(entry.getValue().quantity())
				.append(' ')
				.append(entry.getKey().name());
		}
		sb.append('\n');
		
		return sb.toString();
		
	}
	
	/* (non-Javadoc)
	 * @see state.Orbiter#icon()
	 */
	@Override
	public Image icon()
	{
		return fleet;
	}

	/* (non-Javadoc)
	 * @see state.Orbiter#priority()
	 */
	@Override
	public int priority()
	{
		int base = 10;
		
		// Check if one or other is owner of the star.
		Colony col = destinations.getFirst().colony();
		if(col != null && col.owner() == owner_)
			base += 9;
		else
			base += Galaxy.instance().getEmpires().indexOf(owner_);
		
		return base;
	}
	
}
