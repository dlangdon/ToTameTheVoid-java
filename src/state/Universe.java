package state;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents all the objects in the universe: stars, empires, etc.
 * In particular, it should include all stats required to save/load/serialize the game state.
 * @author Daniel Langdon
 */
public class Universe
{
	private static Universe instance_;
	public static Universe instance()
	{
		return instance_;
	}
	
	private List<Star> stars;
	private List<Empire> empires;
	
	public Universe()
	{
		instance_ = this;
		this.empires = new ArrayList<Empire>();
		this.stars = new ArrayList<Star>();
		
		createStars();
	}
	
	/**
	 * Temporary, only in order to create a static galaxy.
	 */
	public void createStars()
	{
		int x[] =		{ 359, 413, 322, 247, 198, 394, 334, 252, 190, 108, 313, 131, 162, 239, 135, 106, 280, 178, 414, 117, 368, 322, 248, 412, 148, 113, 171,  45, 264, 400, 386, 413, 346, 199, 144, 201, 222, 237,  37 };
		int y[] =		{ 174, 183, 121, 189, 163,  66, 241,  43, 223, 115, 270, 150, 200, 252, 265, 217, 252,  42, 267, 184,  45, 167,  96,  37,  86,  39, 256, 100, 140, 104, 154, 138, 206, 067, 229, 269, 132, 219, 132 };
		int from[] =	{ 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 16, 16, 16, 17, 17, 17, 18, 19, 19, 20, 21, 21, 22, 22, 22, 24, 24, 24, 25, 26, 26, 27, 28, 29, 29, 30, 33 };
		int to[] = 		{ 1, 2, 21, 30, 32, 18, 30, 31, 32, 5, 7, 20, 21, 22, 28, 29, 30, 4, 16, 21, 28, 36, 37, 8, 11, 12, 24, 36, 37, 20, 23, 29, 10, 16, 18, 32, 20, 22, 33, 12, 13, 26, 34, 35, 37, 11, 19, 24, 25, 27, 38, 16, 12, 19, 24, 19, 34, 16, 35, 37, 15, 26, 34, 19, 34, 21, 32, 37, 24, 25, 33, 32, 34, 38, 23, 28, 32, 28, 33, 36, 25, 33, 36, 27, 34, 35, 38, 36, 30, 31, 31, 36 };
		
		if(x.length != y.length || from.length != to.length)
			return;
		
		for(int i=0; i<x.length; i++)
		{
			Star s = new Star(x[i], y[i]);
			s.setName("Star " + i);
			stars.add(s);
		}

		for(int i=0; i<from.length; i++)
		{
			stars.get(from[i]).addLane(stars.get(to[i]));
			stars.get(to[i]).addLane(stars.get(from[i]));
		}
		
		System.out.println("" + stars.size() + " stars created.");
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

}
