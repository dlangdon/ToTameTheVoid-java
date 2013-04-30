/**
 * 
 */
package test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.newdawn.slick.Color;

import state.Colony;
import state.Economy;
import state.Empire;
import state.Star;

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
			float lastTurnProduction = colony.production();
			float lastTurnReserve = economy.reserve();
			
			economy.resetTurn();
			economy.applyGrowth(colonies);
			
			// Check that basic values match, as there is only one colony.
			Assert.assertEquals(economy.totalProduction(), colony.production());
			Assert.assertEquals(economy.totalInfrastructure(), colony.infrastructure());
			Assert.assertEquals(economy.totalMaintenance(), colony.maintenance());
			Assert.assertEquals(economy.bestROI(), colony.returnOfInvestment());
			
			// Check that the posted expenses hold.
			Assert.assertEquals(economy.movements().get(0).amount, -lastTurnMaintenance);
			Assert.assertEquals(economy.movements().get(1).amount, economy.reserve() -lastTurnReserve +lastTurnMaintenance - lastTurnProduction, 1E-5);
			Assert.assertEquals(economy.movements().get(2).amount, lastTurnProduction);
			
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
