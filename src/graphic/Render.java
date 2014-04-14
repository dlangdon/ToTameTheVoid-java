package graphic;

import java.awt.Font;

import org.newdawn.slick.*;

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
	public static AngelCodeFont dialogTitle;
	public static AngelCodeFont dialogSubTitle;
	public static AngelCodeFont dialogText;

	public static Color baseColor = new Color(0, 150, 255);
	public static Color selectColor = new Color(255, 204, 0);
	public static Color highlightColor = new Color(255, 255, 255);

	public static void initialize() throws SlickException
	{
		titles = new TrueTypeFont(new Font("Arial", Font.BOLD, 16), false);
		normal = new TrueTypeFont(new Font("Arial", Font.PLAIN, 12), false);
		dialogTitle = new AngelCodeFont("resources/fonts/space_age_30.fnt", "resources/fonts/space_age_30_0.png");
		dialogSubTitle = new AngelCodeFont("resources/fonts/space_age_20.fnt", "resources/fonts/space_age_20_0.png");
		dialogText = new AngelCodeFont("resources/fonts/neogrey_12.fnt", "resources/fonts/neogrey_12_0.png");
	}
}