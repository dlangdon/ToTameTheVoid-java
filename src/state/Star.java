package state;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Star
{
	// Core star internals
	private String name_;
	private float x_;
	private float y_;
	private float size_;
	private float conditions_;
	private float resources_;
	private List<Star> connected_;
	
	// Drawing internals
	static Image img;

	Star(float x, float y)
	{
		x_ = x;
		y_ = y;
		connected_ = new ArrayList<Star>();
	}
	
	public float x()
	{
		return x_;
	}
	
	public float y()
	{
		return y_;
	}
	
	public String getName()
	{
		return name_;
	}
	
	public void setName(String name)
	{
		this.name_ = name;
	}
	
	public float getSize()
	{
		return size_;
	}
	public float getConditions()
	{
		return conditions_;
	}
	public float getResources()
	{
		return resources_;
	}
	
	public void addLane(Star other)
	{
		connected_.add(other);
	}
	
	public void setParameters(float size, float conditions, float resources)
	{
		this.size_ = size;
		this.conditions_ = conditions;
		this.resources_ = resources;
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		// draw lane
		for(Star s : connected_)
		{
			g.drawLine(x_, y_, s.x_, s.y_);
		}
		
		// Make it so drawing stars is always done in local coordinates.
		g.pushTransform();
		g.translate(x_, y_);
		

		// draw star icon
		img.draw(-16, -16, Color.red);
		
	}

}
