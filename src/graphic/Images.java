package graphic;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum Images
{
	RIGHT_ARROW,
	LEFT_ARROW,
	CORNER_MENU,
	ECONOMY_ICON,
	EMPIRES_ICON,
	SCIENCE_ICON;

	private Image image;

	public Image get()                  { return image;	}
	public static Image get(Images i)   { return i.image;	}

	public static void initialize() throws SlickException
	{
		LEFT_ARROW.image = new Image("resources/left_option.png");
		RIGHT_ARROW.image = LEFT_ARROW.image.getFlippedCopy(true, false);
		CORNER_MENU.image = new Image("resources/corner_menu.png");
		ECONOMY_ICON.image = new Image("resources/economy.png");
		EMPIRES_ICON.image = new Image("resources/empires.png");
		SCIENCE_ICON.image = new Image("resources/science.png");
	}
}
