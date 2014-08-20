/**
 *
 */
package com.github.dlangdon.military;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.github.dlangdon.empire.Economy;
import com.github.dlangdon.state.HQ;
import com.github.dlangdon.state.Star;
import com.github.dlangdon.state.Unit;

/**
 * A slot that builds com.github.dlangdon.military units.
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
			availableUnits_.add(new Ship("Figther", new Image("ship2.png"), 1e-2f));
			availableUnits_.add(new Ship("Colony Ship", new Image("ship1.png"), 1e-1f));

			// Load icon.
			icon_ = new Image("ironFist.png");
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
	 * @see com.github.dlangdon.com.github.dlangdon.state.HQ#getType()
	 */
	@Override
	public String getType()
	{
		return "Shipyard";
	}

	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.state.HQ#availableUnits()
	 */
	@Override
	public List<Unit> availableUnits()
	{
		return availableUnits_;
	}

	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.state.HQ#icon()
	 */
	@Override
	public Image icon()
	{
		return icon_;
	}

	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.state.Orbiter#priority()
	 */
	@Override
	public int priority()
	{
		return 50;
	}


	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.state.HQ#outputExpense()
	 */
	@Override
	protected int outputExpense()
	{
		return constructionExpense;
	}
}
