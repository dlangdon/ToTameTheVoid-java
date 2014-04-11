package ui;

import graphic.Render;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

/**
 * A base class to present a main-scree dialog.
 * Any instance of this dialog exists at a fixed location on the top-left corner, but size can be customized.
 */
public class MainDialog extends BaseDialog
{
// Internals ==========================================================================================================
	private Image[] backgrounds;
	private int repeatX;
	private int repeatY;
	private Image titleSeparator;
	private Image subTitleSeparator;
	private Color textColor;

// Public Methods =====================================================================================================
	public MainDialog() throws SlickException
	{
		backgrounds = new Image[9];
		backgrounds[0] = new Image("resources/dialog/top_left.png");
		backgrounds[1] = new Image("resources/dialog/top.png");
		backgrounds[2] = new Image("resources/dialog/top_right.png");
		backgrounds[3] = new Image("resources/dialog/left.png");
		backgrounds[4] = new Image("resources/dialog/center.png");
		backgrounds[5] = new Image("resources/dialog/right.png");
		backgrounds[6] = new Image("resources/dialog/bottom_left.png");
		backgrounds[7] = new Image("resources/dialog/bottom.png");
		backgrounds[8] = new Image("resources/dialog/bottom_right.png");

		titleSeparator = new Image("resources/dialog/separator_title.png");
		subTitleSeparator = new Image("resources/dialog/separator_subtitle.png");
		textColor = new Color(0, 150, 255);

		setPosition(22, 22);
		setSize(0, 0);
	}

	/**
	 * Sets the position for this dialog.
	 * Since backgrounds have a minimum size, the dialog's size will be the minimum possible size >= than the values passed here.
	 */
	@Override
	public void setSize(float width, float height)
	{
		float newHeight = backgrounds[1].getHeight() + backgrounds[7].getHeight();
		float newWidth = backgrounds[3].getWidth() + backgrounds[5].getWidth();

		repeatX = 0;
		while(newHeight < height)
		{
			repeatX++;
			newHeight += backgrounds[4].getHeight();
		}

		repeatY = 0;
		while(newWidth < width)
		{
			repeatY++;
			newWidth += backgrounds[4].getWidth();
		}

		super.setSize(newHeight, newWidth);
	}

	public void render(GameContainer gc, Graphics g)
	{
		// Calculate how much do we need to strech.
		float x = x();
		float y = y();

		// Paint top line
		backgrounds[0].draw(x, y);
		x += backgrounds[0].getWidth();
		for(int i=0; i<repeatX; i++)
		{
			backgrounds[1].draw(x, y);
			x += backgrounds[1].getWidth();
		}
		backgrounds[2].draw(x, y);
		y+= backgrounds[2].getHeight();

		// Paint middle lines
		for(int j=0; j<repeatY; j++)
		{
			x = x();
			backgrounds[3].draw(x, y);
			x += backgrounds[3].getWidth();
			for(int i=0; i<repeatX; i++)
			{
				backgrounds[4].draw(x, y);
				x += backgrounds[4].getWidth();
			}
			backgrounds[5].draw(x, y);
			y+= backgrounds[5].getHeight();
		}

		// Paint bottom line
		x = x();
		backgrounds[6].draw(x, y);
		x += backgrounds[6].getWidth();
		for(int i=0; i<repeatX; i++)
		{
			backgrounds[7].draw(x, y);
			x += backgrounds[7].getWidth();
		}
		backgrounds[8].draw(x, y);
	}

	public float drawTitle(String title)
	{
		Render.dialogTitle.drawString(x() + 105, y() + 38, title);
		titleSeparator.draw(x() + 77, y() + 59);
		return y() + 102;
	}

	/**
	 * Renders a subtitle. Coordinates already account for titles, so 0,0 corresponds to the first valid location for a subtitle.
	 * @param x Coordinate to put the subtitle. 0 corresponds to a valid place, as padding is introduced.
	 * @param y
	 * @param title
	 */
	public float drawSubtitle(float x, float y, String title)
	{
		Render.dialogSubTitle.drawString(x + 73, y, title);
		subTitleSeparator.draw(x + 50, y + 22);
		return y + 40;
	}

	/**
	 * Draws a piece of text in the base color for this dialog.
	 * @param toLeft if true, string is drawn up to the specified x, instead of from it.
	 * @return a recommended Y to put the next element. If hintX is true, a recommended X is returned instead.
	 */
	public float drawText(float x, float y, String text, boolean toLeft, boolean hintX)
	{
		float width = Render.dialogText.getWidth(text);
		float realx = (toLeft ? x-width : x);
		Render.dialogText.drawString(realx, y, text, textColor);
		return hintX ? realx + width : y + Render.dialogText.getLineHeight();
	}

	public float drawText(float x, float y, String text)
	{
		return drawText(x, y, text, false, false);
	}


	@Override
	public boolean isCursorInside()
	{
		return false;
	}

	@Override
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		return false;
	}
}
