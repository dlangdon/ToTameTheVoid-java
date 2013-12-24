package graphic;

import java.awt.Font;

import org.newdawn.slick.TrueTypeFont;

/**
 * A helper class to hold common resources and definitions needed by several graphical objects.
 * The idea is to have a consistent style centralized here.
 * @author Daniel Langdon
 */
public class Render
{
	public enum Visibility { VISIBLE, REMEMBERED, REACHABLE, HIDDEN }

	public static final int SELECTED = 0x01; 
	public static final int SMALL 	= 0x02;
	public static final int BIG 		= 0x04;
	
	public static TrueTypeFont titles;
	public static TrueTypeFont normal;

	public static void init()
	{
		titles = new TrueTypeFont(new Font("Arial", Font.BOLD, 16), false);
		normal = new TrueTypeFont(new Font("Arial", Font.PLAIN, 12), false);
	}
	
}
