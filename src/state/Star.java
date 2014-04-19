package state;

import empire.Empire;
import galaxy.structure.Place;
import graphic.Camera;
import graphic.Images;
import graphic.Render;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import ui.UIListener;

import java.util.ArrayList;
import java.util.List;

public class Star extends Place implements UIListener
{
	// Statics ============================================================================================================
	private static List<Star> all = new ArrayList<>();
	public static List<Star> all() { return all;	}

	// Internals ==========================================================================================================
	// Core star internals
	private int index_;               // Mainly needed for persistence && communications
	private String name_;
	private Vector2f pos;
	private float size_;
	private float conditions_;
	private float resources_;
	private Empire owner_;

// Public Methods =====================================================================================================

	public Empire owner() {	return owner_;	}
	public void setOwner(Empire owner)	{ this.owner_ = owner;	}

	public Star(int index, float x, float y)
	{
		index_ = index;
		pos = new Vector2f(x, y);
		all.add(this);
	}

	public float x()	               { return pos.x; }
	public float y()	               { return pos.y; }
	public Vector2f getPos()         { return pos;	}
	public String name()	            { return name_; }
	public void setName(String name)	{ this.name_ = name;	}
	public float size()              { return size_;	}
	public float conditions()        { return conditions_;	}
	public float resources()         { return resources_; }

	public void setParameters(float size, float conditions, float resources)
	{
		this.size_ = size;
		this.conditions_ = conditions;
		this.resources_ = resources;
	}

	public void render(GameContainer gc, Graphics g, Render.Visibility visibility)
	{
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, pos);

		// draw star icon
		Color color = owner_ != null ? owner_.color() : Color.white;
		color = visibility != Render.Visibility.VISIBLE ? color.darker(0.6f) : color;
		Images.STAR.get().draw(-16, -16, color);

		g.popTransform();
	}

	/**
	 * @return the index
	 */
	public int index()
	{
		return index_;
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		// Get a pixel distance centered on this star.
		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(pos));
		return (local.x * local.x <= 144 && local.y * local.y <= 144);
	}
}
