package ui.widget;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import java.util.LinkedList;

/**
 * Basic class to represent graphical widgets.
 * The widget scope is simple; each widget store it absolute position and size, as well as a dependency tree that maps child widgets.
 * The hierarchy is traversed in two circumstances:
 * 1.- Whenever the mouse moves, in order to determine if a new widget is below it.
 * 2.- Whenever a widget is hidden or shown, since it might change what is below the mouse. This is done by "moving" the mouse to the same position.
 *
 * For any other input, the widget under the mouse has focus, and receives other events.
 */
public abstract class Widget
{
	private static Widget _underMouse;
	public static Widget underMouse()   { return _underMouse; }

//	public enum Corner { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT }
	private float x;
	private float y;
	private float height;
	private float width;
	private LinkedList<Widget> children = new LinkedList<>();

	public float x()                                   { return x; }
	public float y()                                   { return y; }
	public float height()                              { return height; }
	public float width()                               { return  width; }
	public void setPosition(float x, float y)          { this.x = x; this.y = y; }

	public Widget()
	{
	}

	public Widget(Widget parent)
	{
		parent.children.add(this);
	}

	/**
	 * Size values are implementation dependant.
	 * In general terms, either we need to know how to render or we know but we need to remember what we rendered in order to click.
	 * As a result, the widget will take this as a hint, but might override it after rendering.
	 */
	public void setSize(float width, float height)     { this.height = height; this.width = width; }

	public void mouseClick(int button, int delta)      {}

	/**
	 * Updates internal state of the cursor, used to determine hovering over elements, popups, etc.
	 * @return True if the mouse switched between two different elements by this movement, and any counter should be reset.
	 */
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		if(newx >= x() && newx <= x()+width() && newy >= y() && newy <= y()+height())
			_underMouse = this;

		for(Widget child : children)
			child.moveCursor(oldx, oldy, newx, newy);

		return false;
	}

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
	}
}
