package graphic;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public enum Images
{
	RIGHT_ARROW,
	LEFT_ARROW;

	private Image _image;

	public Image get()                  { return _image;	}
	public static Image get(Images i)   { return i._image;	}

	public static void initialize() throws SlickException
	{
		LEFT_ARROW._image = new Image("resources/left_option.png");
		RIGHT_ARROW._image = LEFT_ARROW._image.getFlippedCopy(true, false);
	}
}
