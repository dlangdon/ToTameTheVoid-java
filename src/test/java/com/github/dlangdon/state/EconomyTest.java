/**
 *
 */
package com.github.dlangdon.state;

import java.util.HashSet;
import java.util.Set;

import com.github.dlangdon.empire.Economy;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.newdawn.slick.Color;

import com.github.dlangdon.empire.Empire;

/**
 * @author Daniel Langdon
 *
 */
public class EconomyTest
{
	Economy economy;
	Empire empire;
	Star richStar;
	Star averageStar;
	Star poorStar;
	Colony colony;
	Set<Colony> colonies;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		Economy.init();

		empire = new Empire("Test Empire", Color.red);
		economy = new Economy();

		richStar = new Star(1, 0.0f, 0.0f);
		poorStar = new Star(1, 0.0f, 0.0f);
		averageStar = new Star(1, 0.0f, 0.0f);

		richStar.setParameters(1.0f, 1.0f, 1.0f);
		averageStar.setParameters(1.0f, 1.0f, 1.0f);
		poorStar.setParameters(1.0f, 1.0f, 1.0f);

		colony = new Colony(richStar, empire);
		colonies = new HashSet<Colony>();
		colonies.add(colony);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testMargins()
	{
		// Check that the maximum infrastructure is being calculated correctly.
//		Assert.assertEquals(1.3125f, colony.maxInfrastructure());
//		Assert.assertEquals(0.0117669944f, colony.production());
//		QCOMPARE(colony3.maxInfrastructure(), MIN_INFRASTRUCTURE);
	}

	@Test
	public void testNormalGrowth()
	{
		// Turn -1 needs to be run in order to have good calculations for production.
		economy.applyGrowth(colonies);

		for(int i=0; i<100; i++)
		{
			// Get some temporary variables.
			float lastTurnMaintenance = economy.totalMaintenance();
			double lastTurnProduction = colony.production();
			float lastTurnReserve = economy.reserve();

			economy.resetTurn();
			economy.applyGrowth(colonies);

			// Check that basic values match, as there is only one colony.
			Assert.assertEquals(economy.totalProduction(), colony.production(), 1e-6);
			Assert.assertEquals(economy.totalInfrastructure(), colony.infrastructure(), 1e-6 );
			Assert.assertEquals(economy.totalMaintenance(), colony.maintenance(), 1e-6);
			Assert.assertEquals(economy.bestROI(), colony.returnOfInvestment(), 1e-6);

			// Check that the posted expenses hold.
			Assert.assertEquals(economy.movements()[0], -lastTurnMaintenance);
			Assert.assertEquals(economy.movements()[1], economy.reserve() -lastTurnReserve +lastTurnMaintenance - lastTurnProduction, 1E-5);
			Assert.assertEquals(economy.movements()[2], lastTurnProduction, 1e-6);

//			System.out.format("\nTurn %d: prod=%3.4f, main=%3.4f", i, economy.totalProduction(), economy.totalMaintenance());
		}

		// Check that it converges
		// TODO
	}

	@Test
	public void testEconomy()
	{
//		fail();

	}


}
