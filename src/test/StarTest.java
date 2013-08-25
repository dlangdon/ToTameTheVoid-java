/**
 * 
 */
package test;

import static org.junit.Assert.*;

import military.Shipyard;

import org.junit.Test;

import event.GameEventQueue;

import state.Fleet;
import state.Star;

/**
 * @author Daniel Langdon
 *
 */
public class StarTest
{
	@Test
	public void orbiterClassTest()
	{
		GameEventQueue queue = new GameEventQueue();
		Star s = new Star(0, 0, 0);
		Shipyard sy = new Shipyard(s);
		
		assertNotNull("No shipyard present", s.getOrbiter(Shipyard.class));
		assertNull("Invalid fleet present", s.getOrbiter(Fleet.class));
	}

}
