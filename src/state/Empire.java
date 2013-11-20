package state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.Color;

public class Empire
{
	class PlaceView extends Place
	{
		private int lastUpdate_;
	}
	
// Statics ============================================================================================================	
	public static final float DISTRUST = 0.0f;
	public static final float CEASE_FIRE = 0.14f;
	public static final float OPEN_TRADE = 0.28f;
	public static final float CULTURAL_COOPERATION = 42f;
	public static final float SANCTUARY = 0.57f;
	public static final float RESOURCE_SHARING = 71f;
	public static final float OPEN_BORDERS = 85f;
	public static final float BROTHERHOOD = 1.0f;

	private static List<Empire> all = new ArrayList<Empire>();
	private static Empire playerEmpire = null;
	public static Empire getPlayerEmpire()
	{
		return playerEmpire;
	}
	public static void setPlayerEmpire(Empire playerEmpire)
	{
		Empire.playerEmpire = playerEmpire;
	}	
	public static List<Empire> all()
	{
		return all;
	}
	
// Internals ==========================================================================================================
	private String name_;
	private Color color_;
	private Economy economy_;
	private HashSet<Colony> colonies_;
	private HashMap<Empire, Double> trust;
	private Map<Place, PlaceView> galaxyView;
	
// Public Methods =====================================================================================================

 	public Empire(String name_, Color color_)
	{
		super();
		this.name_ = name_;
		this.color_ = color_;
		colonies_ = new HashSet<Colony>();
		economy_ = new Economy();
		trust = new HashMap<Empire, Double>();
		galaxyView = new HashMap<Place, PlaceView>();

		all.add(this);
	}

	public void addColony(Colony colony)
	{
		colonies_.add(colony);
	}

	public String name()
	{
		return name_;
	}

	public Color color()
	{
		return color_;
	}
	
	/**
	 * @return The current economy state for this empire.
	 */
	public Economy getEconomy()
	{
		return economy_;
	}

	public Set<Colony> getColonies()
	{
		return colonies_;
	}

	/**
	 * Returns the level of trust this empire is willing to confer to the other empire.
	 * @param other Opposite empire.
	 * @return A number between 0 and 100 measuring trust.
	 */
	public double trustLevel(Empire other)
	{
		if(this == other)
			return 100;

		Double aux = trust.get(other);
		return aux == null ? 0 : aux;
	}
	
	/**
	 * Returns the level of reciprocal truth between two empires.
	 * @param other The other empire.
	 * @return A number between 0 and 100 measuring trust.
	 */
	public double reciprocalTrust(Empire other)
	{
		return Math.min(this.trustLevel(other), other.trustLevel(this));
	}

	public void updateVisible(int turn, Place p)
	{
		// Get what we knew (or not) from that location.
		PlaceView stored = galaxyView.get(p);
		if(stored == null)
			stored = new PlaceView();
		
		// Update what we see given the current empire.
		if(stored.lastUpdate_ < turn)
		{
			stored.allPlaceables().clear();
			for(Placeable i: p.allPlaceables())
			{
				// Hide agents.
				if((i instanceof Fleet) && ((Fleet)i).type() == "AGENT" && i.owner() != this)
					continue;
				
				// TODO Summarize fleets from other empires as single fleet.
				stored.allPlaceables().add(i);
				
				// If star, put visibility of lanes too.
				if(p instanceof Star)
				{
					
				}
			}			
		}
	}
}
