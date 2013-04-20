package state;

import main.Camera;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Star
{
// Internals ==========================================================================================================
	// Core star internals
	private int index_;
	private String name_;
	Vector2f pos;
	private float size_;
	private float conditions_;
	private float resources_;
	private Colony colony;
	
	// Drawing internals
	public static Image img;

// Public Methods =====================================================================================================
	
	Star(int index, float x, float y)
	{
		index_ = index;
		pos = new Vector2f(x, y);
	}
	
	public float x()
	{
		return pos.x;
	}
	
	public float y()
	{
		return pos.y;
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
		Camera.instance().pushLocalTransformation(g, pos);

		// draw star icon
		img.draw(-16, -16, Color.red);
		
		g.popTransform();
	}

	/**
	 * @return the index
	 */
	public int index()
	{
		return index_;
	}
	

	public void setColony(Colony colony)
	{
		this.colony = colony;
	}
	
	public Colony getColony()
	{
		return colony;
	}
}
