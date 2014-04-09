package ui.widget;

public abstract class Widget
{
//	public enum Corner { TOP_RIGHT, TOP_LEFT, BOTTOM_RIGHT, BOTTOM_LEFT }

	private float x;
	private float y;
	private float height;
	private float width;

	public float x()                                   { return x; }
	public float y()                                   { return y; }
	public float height()                              { return height; }
	public float width()                               { return  width; }
	public void setPosition(float x, float y)          { this.x = x; this.y = y; }
	public void setSize(float width, float height)     { this.height = height; this.width = width; }
}
