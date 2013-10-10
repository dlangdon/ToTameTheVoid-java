package galaxy.generation;


import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.SlickException;

import state.Empire;
import state.Star;

/**
 * Class that represents all the objects in the universe: stars, empires, etc.
 * In particular, it should include all stats required to save/load/serialize the game state.
 * @author Daniel Langdon
 */
public class Galaxy
{
	private static Galaxy instance_;
	public static Galaxy instance()
	{
		return instance_;
	}

// Internals ==========================================================================================================	
	List<Star> stars;
	List<Empire> empires;
	Empire playerEmpire;

// Public Methods =====================================================================================================
	public Galaxy() throws SlickException
	{
		instance_ = this;
		this.empires = new ArrayList<Empire>();
		this.stars = new ArrayList<Star>();
		
		NascentGalaxy ng = new NascentGalaxy(600, 400, 4.0f);

//		ng.addForce(new StaticGalaxyCreator());

		ng.addForce(new SimpleBlobCreator(30, 4, 15), true);
		ng.addForce(new SimplePointCreator(5, 50), true);
		ng.addForce(new DelaunayLaneGenerator(0.15f), true);
		ng.addForce(new MinimumSpanningTreeForce(), true);
		ng.addForce(new StartingLocationFinder(5, 50), true);
		ng.addForce(new RandomStarGenerator(), true);
		
		ng.addForce(new EmpireInitialiazer(5), false);
		
		ng.blossom();
	}

	/**
	 * @return A list of all stars.
	 */
	public List<Star> getStars()
	{
		return stars;
	}

	/**
	 * @return A list of all empires.
	 */
	public List<Empire> getEmpires()
	{
		return empires;
	}

	/**
	 * @return the empire corresponding to the local player
	 */
	public Empire getPlayerEmpire()
	{
		return playerEmpire;
	}
}
