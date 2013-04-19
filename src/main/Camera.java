package main;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;


/**
 * Class that represents a camera, centered in a specific location in world coordinates.
 * This class provides the necessary transformations (displacement and scale) needed to render world objects.
 * Hud and other objects can be rendered normally, since they don't depend on the world at all.
 * Pixel resolution is assumed.
 * @author Daniel Langdon
 */
public class Camera
{
	private static Camera instance_;
	public static Camera instance()
	{
		return instance_;
	}
	
	Vector2f world;			// World size
	Vector2f resolution;		// Half the Screen size
	Vector2f view;				// World coordinate of the screen origin.
	int scale;					// Multiplier for scale: 1, 2 or 4
	
	/**
	 * Creates a new camera.
	 * @param screen Vector that indicates the size of the screen (in screen coordinates).
	 * @param world Vector that indicates the size of the world (in world coordinates).
	 */
	public Camera(Vector2f screen, Vector2f world)
	{
		instance_ = this;
		this.resolution = new Vector2f(screen).scale(0.5f);
		this.world = new Vector2f(world);
		this.view = new Vector2f(0.0f, 0.0f);
		this.scale = 1;
	}
	
	/**
	 * Moves the camera around a certain number of pixels.
	 * The movement should be equally fast independently of the current scale (this may change)
	 * @param x
	 * @param y
	 */
	public void move(Vector2f delta)
	{
		view.add(delta);
	}
	
	/**
	 * Modifies the current zoom. 
	 * @param in True if the interface should zoom in, false if it should zoom out.
	 */
	public void zoom(boolean in)
	{
		if(in)
		{
			if(scale <= 2)
				scale *= 2;
		}
		else
		{
			if(scale >= 2)
				scale /= 2;
		}
		System.out.println("new zoom: " + scale + ", view at: " + view + ", resolution: " + resolution);
	}
	
	Vector2f worldToScreen(Vector2f world)
	{
		return new Vector2f(world).sub(view).scale(scale);
	}
	
	void centerOnWorld(Vector2f world)
	{
		view = world;
	}

	/**
	 * Pushes a new transformation matrix in terms of the world's coordinates.
	 * @param g The graphic context.
	 */
	void pushWorldTransformation(Graphics g)
	{
		g.pushTransform();
		
		// Always zoom in the center of the screen.
		g.translate(resolution.getX(), resolution.getY());
		g.scale(scale, scale);
		g.translate(-resolution.getX(), -resolution.getY());

		// Move the view
		g.translate(-view.getX(), -view.getY());
	}
	
	/**
	 * Allows objects to draw on local screen coordinates, ignoring size.
	 */
	public void pushLocalTransformation(Graphics g, Vector2f pos)
	{
		g.pushTransform();
		
		// Center on the current object.
		g.translate(pos.x, pos.y);
		
		// Undo scaling of graphics!
		g.scale(1.0f/scale, 1.0f/scale);
		
	}
}
