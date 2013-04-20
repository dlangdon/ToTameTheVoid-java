package main;
import org.newdawn.slick.Color;
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
		this.view = new Vector2f(-world.x/2, -world.y/2);
		this.scale = 1;
		
		move(new Vector2f(0.0f, 0.0f));
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
		
		// Check boundaries in world coordinates.
		Vector2f topLeftDif = new Vector2f(0.0f, 0.0f).scale(1.0f/scale).add(view);
		Vector2f bottomRightDif = new Vector2f(resolution).scale(2.0f/scale).add(view).sub(world).negate();

		if(topLeftDif.x <= 0.0 && bottomRightDif.x <= 0.0)
			view.x = (topLeftDif.x + bottomRightDif.x)/2;
		else if(topLeftDif.x < 0.0)
			view.x -= topLeftDif.x;
		else if(bottomRightDif.x < 0.0)
			view.x += bottomRightDif.x;
		
		if(topLeftDif.y <= 0.0 && bottomRightDif.y <= 0.0)
			view.y = (topLeftDif.y + bottomRightDif.y)/2;
		else if(topLeftDif.y < 0.0)
			view.y -= topLeftDif.y;
		else if(bottomRightDif.y < 0.0)
			view.y += bottomRightDif.y;
		
//		System.out.println("Boundary differences: (" 
//													+ topLeftDif.x + ", " + topLeftDif.y + ") --> (" 
//													+ bottomRightDif.x + ", " + bottomRightDif.y + "), view = ("
//													+ view.x + ", " + view.y + ")");
	}
	
	/**
	 * Modifies the current zoom. 
	 * @param in True if the interface should zoom in, false if it should zoom out.
	 */
	public void zoom(boolean in, Vector2f centerOnScreen)
	{
		// Detect the world coordinate of the screen point.
		Vector2f centerOnWorld = new Vector2f(centerOnScreen).scale(1.0f/scale).add(view);

		// Modify the zoom.
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
		
		// After zooming, move the view so that the point gets to the center.
		view = centerOnWorld.sub(new Vector2f(resolution).scale(1.0f/scale));
		
		// Do a null move, so that borders are checked.
		move(new Vector2f(0.0f, 0.0f));
		System.out.println("new zoom: " + scale + ", view at: " + view + ", resolution: " + resolution);
	}
	
	Vector2f worldToScreen(Vector2f world)
	{
		return new Vector2f(world).sub(view).scale(scale);
	}

	Vector2f screenToWorld(Vector2f screen)
	{
		return new Vector2f(screen).scale(1.0f/scale).add(view);
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
//		g.translate(resolution.getX(), resolution.getY());
		g.scale(scale, scale);
//		g.translate(-resolution.getX(), -resolution.getY());

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
	
	public void drawWorldLimits(Graphics g)
	{
		g.setColor(Color.white);
		g.drawRect(0, 0, world.x, world.y);
	}

	/**
	 * @return The screen coordinates of the center of the screen. This object should not be modified.
	 */
	public Vector2f getScreenCenter()
	{
		return resolution;
	}
}
