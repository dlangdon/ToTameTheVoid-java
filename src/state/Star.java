package state;

import event.GameEventQueue;
import graphic.Camera;
import graphic.UIListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Star implements UIListener
{
// Internals ==========================================================================================================
	// Core star internals
	private int index_;
	private String name_;
	private Vector2f pos;
	private float size_;
	private float conditions_;
	private float resources_;
	private Colony colony_;
	private List<Fleet> inOrbit;

	// Drawing internals
	public static Image img;
	public static Image ihqIcon;

// Public Methods =====================================================================================================

	public Star(int index, float x, float y)
	{
		index_ = index;
		pos = new Vector2f(x, y);
		inOrbit = new ArrayList<Fleet>();
	}

	public float x()
	{
		return pos.x;
	}

	public float y()
	{
		return pos.y;
	}

	public Vector2f getPos()
	{
		return pos;
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
		Color color = colony_ != null ? colony_.owner().color() : Color.white;
		
		img.draw(-16, -16, color);

		// If it has an HQ, draw it.
		if(colony_ != null && colony_.ihq() != null)
		{
			ihqIcon.draw(10, 10, color);
		}
		
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
		this.colony_ = colony;
	}

	public Colony colony()
	{
		return colony_;
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		// Get a pixel distance centered on this star.
		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(pos));

		if (local.x * local.x <= 144 && local.y * local.y <= 144)
		{
			// I'm in the scar icon (32x32)
			return true;
		}

		return false;
	}

	/**
	 * Receives a signal when a task fleet arrives on the system.
	 * 
	 * @param fleet
	 *           The task fleet that arrived.
	 */
	public void arrive(Fleet fleet)
	{
		inOrbit.add(fleet);
		Collections.sort(inOrbit);
		GameEventQueue.instance().addLocationToCheck(this);
	}

	/**
	 * Receives a signal when a task fleet leaves the system.
	 * 
	 * @param fleet
	 *           The task fleet that departed.
	 */
	public void leave(Fleet fleet)
	{
		inOrbit.remove(fleet);
	}

	public int getDock(Fleet tf)
	{
		return inOrbit.indexOf(tf);
	}

	public List<Fleet> getFleetsInOrbit()
	{
		return inOrbit;
	}
}
