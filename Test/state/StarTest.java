/**
 * 
 */
package state;

import static org.junit.Assert.*;

import military.Shipyard;

import org.junit.Test;

import simulation.Simulator;

/**
 * @author Daniel Langdon
 *
 */
public class StarTest
{
	@Test
	public void orbiterClassTest()
	{
		Simulator queue = new Simulator();
		Star s = new Star(0, 0, 0);
		Shipyard sy = new Shipyard(s);
		
		assertNotNull("No shipyard present", s.getPlaceable(Shipyard.class));
		assertNull("Invalid fleet present", s.getPlaceable(Fleet.class));
	}

}
