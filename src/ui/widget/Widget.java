package ui.widget;

import org.lwjgl.input.Mouse;
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
public class Widget
{
// Statics ==============================================================================================
	protected static Widget NONE = new Widget();
	protected static Widget _underMouse = NONE;
	protected static long _milisMouseHover = 0;
	protected static long _milisMouseDown = 0;
	public static Widget underMouse() { return _underMouse; }

//	public enum Corner { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT }
	private float x;
	private float y;
	private float height;
	private float width;
	protected LinkedList<Widget> children = new LinkedList<>();

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
	public void setSize(float width, float height)
	{
		this.height = height; this.width = width;
	}

	/**
	 * Updates internal state of the cursor, used to determine hovering over elements, popups, etc.
	 * If the mouse moves internally in or out of a given widget, or between internal elements inside the widget, it is up to him to reset time counters so that hover and mousedown works appropriately.
	 * @return True if a widget was found (and stored as Widget.underMouse).
	 */
	public boolean moveCursor(int oldx, int oldy, int newx, int newy)
	{
		// Check children first
		for(Widget child : children)
			if(child.moveCursor(oldx, oldy, newx, newy))
				return true;

		boolean isMe = (newx >= x() && newx <= x()+width() && newy >= y() && newy <= y()+height());
		if(isMe)
		{
			if(_underMouse != this)
				_milisMouseHover = 0;
			_underMouse = this;
			return true;
		}
		else if(_underMouse == this)
			_underMouse = NONE;
		return false;
	}

	/**
	 * Notifies the pass of time to this widget.
	 * Potentially useful for animations, but for now only for timed-clicks and hovers, so it's ok to only call update on the widget under the mouse.
	 * TODO If it ever becomes important, will need to allow widgets to subscribe to certain events, so we don't need to loop over all widgets.
	 * @param delta miliseconds since the last update.
	 */
	public void update(int delta)
	{
		if(Mouse.isButtonDown(0))
			_milisMouseDown += delta;
		else
			_milisMouseDown = 0;
		_milisMouseHover += delta;
	}

	/**
	 * Registers a click on the widget
	 * @param button 0 for left button, 1 for right, etc.
	 * @return true if the click was eaten by this widget. This is true by default, but widget can decide to be 'transparent'.
	 */
	public boolean mouseClick(int button)
	{
		return (this == NONE) ? false : true;
	}

	/**
	 * Renders the widget
	 */
	public void render(GameContainer gc, Graphics g)
	{
	}
}
