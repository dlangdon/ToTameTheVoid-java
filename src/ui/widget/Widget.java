package ui.widget;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import java.util.LinkedList;

public abstract class Widget
{
//	public enum Corner { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT }

	private float x;
	private float y;
	private float height;
	private float width;
//	private LinkedList<Widget> children = new LinkedList<>();

	public float x()                                   { return x; }
	public float y()                                   { return y; }
	public float height()                              { return height; }
	public float width()                               { return  width; }
	public void setPosition(float x, float y)          { this.x = x; this.y = y; }

	/**
	 * Size values are implementation dependant.
	 * In general terms, either we need to know how to render or we know but we need to remember what we rendered in order to click.
	 * As a result, the widget will take this as a hint, but might override it after rendering.
	 */
	public void setSize(float width, float height)     { this.height = height; this.width = width; }

//	public void addChild(Widget child)                 { children.add(child); }

	/**
	 * Renders the widget
	 */
	public void render(GameContainer gc, Graphics g)
	{

	}

	/**
	 * Handles a click. Returns true if the event was accounted for, false if we didn't use it.
	 * Unless overloaded, a visible widget will catch a click inside its boundaries.
	 * TODO Unify mouse over and click mechanism, the ones on main dialog are different! (and look better than this). Track mouse position globally.
	 */
	public boolean screenCLick(float x, float y, int button)
	{
		return false;
//		return (x >= x() && x <= x()+width() && y >= y() && y <= y()+height());
	}
}
