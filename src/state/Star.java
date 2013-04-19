package state;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Star
{
	// Core star internals
	private int index_;
	private String name_;
	private float x_;
	private float y_;
	private float size_;
	private float conditions_;
	private float resources_;
	
	// Drawing internals
	public static Image img;

	Star(int index, float x, float y)
	{
		index_ = index;
		x_ = x;
		y_ = y;
	}
	
	public float x()
	{
		return x_;
	}
	
	public float y()
	{
		return y_;
	}
	
	public String name()
	{
		return name_;
	}
	
	public void setName(String name)
	{
		this.name_ = name;
	}
	
	public float size()
	{
		return size_;
	}
	public float conditions()
	{
		return conditions_;
	}
	public float resources()
	{
		return resources_;
	}
	
	public void setParameters(float size, float conditions, float resources)
	{
		this.size_ = size;
		this.conditions_ = conditions;
		this.resources_ = resources;
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		// Make it so drawing stars is always done in local coordinates.

		// draw star icon
		img.draw(-16, -16, Color.red);
		
//		g.popTransform();
	}

	/**
	 * @return the index
	 */
	public int index()
	{
		return index_;
	}
}
