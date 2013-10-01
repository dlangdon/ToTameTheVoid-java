/**
 * 
 */
package military;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import state.Economy;
import state.HQ;
import state.Star;
import state.Unit;

/**
 * A slot that builds military units.
 * @author Daniel Langdon
 */
public class Shipyard extends HQ
{
	private static int maintenanceExpense;
	private static int upgradeExpense;
	private static int constructionExpense;
	private static Image icon_;
	private static List<Unit> availableUnits_;
	
	public static void init()
	{
		maintenanceExpense = Economy.registerCause("Shipyard Maintenance");
		upgradeExpense = Economy.registerCause("Shipyard Expansion");
		constructionExpense = Economy.registerCause("Fleet Construction");
		availableUnits_ = new ArrayList<Unit>();
		
		try
		{
			// Add units.
			availableUnits_.add(new Ship("Figther", new Image("resources/ship2.png"), 1.0f));
			availableUnits_.add(new Ship("Colony Ship", new Image("resources/ship1.png"), 10.0f));
			availableUnits_.add(new Ship("Scout", new Image("resources/ship1.png"), 10.0f));
			
			// Load icon.
			icon_ = new Image("resources/ironFist.png");
		}
		catch (SlickException e)
		{
			System.out.println("Problem initializing resources.");
			e.printStackTrace();
		}
	}

	/**
	 * @param colony
	 */
	public Shipyard(Star location)
	{
		super(location);
	}
	
	/* (non-Javadoc)
	 * @see state.HQ#getType()
	 */
	@Override
	public String getType()
	{
		return "Shipyard";
	}

	/* (non-Javadoc)
	 * @see state.HQ#availableUnits()
	 */
	@Override
	public List<Unit> availableUnits()
	{
		return availableUnits_;
	}

	/* (non-Javadoc)
	 * @see state.HQ#icon()
	 */
	@Override
	public Image icon()
	{
		return icon_;
	}

	/* (non-Javadoc)
	 * @see state.Orbiter#priority()
	 */
	@Override
	public int priority()
	{
		return 50;
	}


	/* (non-Javadoc)
	 * @see state.HQ#outputExpense()
	 */
	@Override
	protected int outputExpense()
	{
		return constructionExpense;
	}
}
