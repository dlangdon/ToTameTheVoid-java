package graphic;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
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
	Vector2f padding;			// Half the Screen size
	Vector2f view;				// World coordinate of the screen origin.
	float scale_;				// Multiplier for scale: 1, 2 or 4
	
	/**
	 * Creates a new camera.
	 * @param screen Vector that indicates the size of the screen (in screen coordinates).
	 * @param world Vector that indicates the size of the world (in world coordinates).
	 */
	public Camera(Vector2f screen, Vector2f world, Vector2f padding)
	{
		instance_ = this;
		this.resolution = new Vector2f(screen).scale(0.5f);
		this.world = new Vector2f(world);
		this.view = new Vector2f(-world.x/2, -world.y/2);
		this.scale_ = 1;
		this.padding = padding;
		
		centerOnWorld(new Vector2f(world).scale(0.5f));
	}
	
	/**
	 * Moves the camera around a certain number of units in world coordinates.
	 */
	public void move(Vector2f delta)
	{
		view.add(delta);
		
		Vector2f topLeftDif = screenToWorld(padding);
		Vector2f bottomRightDif = screenToWorld(new Vector2f(resolution).scale(2.0f).sub(padding)).sub(world).negate();

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
	}
	
	/**
	 * Modifies the current zoom. 
	 * @param in True if the interface should zoom in, false if it should zoom out.
	 */
	public void zoom(boolean in, Vector2f centerOnScreen)
	{
		// Detect the world coordinate of the screen point BEFORE making changes!
		Vector2f toCenter = screenToWorld(centerOnScreen);

		// Modify the zoom.
		if(in)
		{
			if(scale_ > 2)
				return;
			scale_ *= 2;
		}
		else
		{
			if(scale_ < 2)
				return;
			scale_ /= 2;
		}

		// After zooming, move the view so that the point gets to the center.
		if(scale_ == 1 && resolution.x * 2 > world.x && resolution.y * 2 > world.y)
			centerOnWorld(new Vector2f(world).scale(0.5f));
		else
			centerOnWorld(toCenter);
	}
	
	public Vector2f worldToScreen(Vector2f world)
	{
		return new Vector2f(world).sub(view).scale(scale_).add(padding);
	}

	public Vector2f screenToWorld(Vector2f screen)
	{
		return new Vector2f(screen).sub(padding).scale(1.0f/scale_).add(view);
	}
	
	public void centerOnWorld(Vector2f world)
	{
		Vector2f center = screenToWorld(resolution.copy());
		move(world.copy().sub(center));
	}


	/**
	 * Pushes a new transformation matrix in terms of the world's coordinates.
	 * @param g The graphic context.
	 */
	void pushWorldTransformation(Graphics g)
	{
		g.pushTransform();
		g.translate(padding.getX(), padding.getY());
		g.scale(scale_, scale_);
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
		g.scale(1.0f/scale_, 1.0f/scale_);
	}
	
	
	public void drawWorldLimits(Graphics g)
	{
		g.setColor(Color.white);
		g.drawRect(0, 0, world.x, world.y);
	}

	/**
	 * @return The screen coordinates of the center of the screen.
	 */
	public Vector2f getScreenCenter()
	{
		return resolution.copy();
	}

	public float scale()
	{
		return scale_;
	}

	/**
	 * Handles input in order to move the camera.
	 * Move the camera around. Movement is peculiar in what feels right.
	 * We don't want to move in world coordinates, as that is affected by zoom and feels wrong to change speeds.
	 * We also don't want a stable per-pixel speed, as it feels sluggish on high resolutions.
	 * There does not seem to be a way on LWJGL to get pixel density, so we can only hint at it by resolution.
	 * @param timeDelta The time elapses since the last time the camera was updated.
	 */
	public void update(float timeDelta)
	{
		Vector2f displacement = Camera.instance().getScreenCenter().scale(timeDelta / 1000.0f);
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Mouse.getX() < 5)
      	displacement.x *= -1;
		else if(!Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && Mouse.getX() <= Display.getWidth() - 5)
      	displacement.x = 0.0f;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Mouse.getY() > Display.getHeight() - 5)
      	displacement.y *= -1;
		else if(!Keyboard.isKeyDown(Keyboard.KEY_DOWN) && Mouse.getY() >= 5)
      	displacement.y = 0.0f;
		Camera.instance().move(displacement);
	}

	/**
	 * Ensures that the current view has enough margins to all sides.
	 * If not, it moves the view just enough to comply with the requirements.
	 * When trying to comply, the normal camera limits apply (see Camera.move())
	 */
	public void ensureVisible(Vector2f origin, float top, float right, float bottom, float left)
	{
		Vector2f screen = worldToScreen(origin);
		Vector2f adjustment = new Vector2f(0.0f, 0.0f);
		if(screen.x + right > 2*resolution.x)
			adjustment.x = 2*resolution.x - right - screen.x;
		if(screen.x - left < 0)
			adjustment.x = left - screen.x;
		if(screen.y + bottom > 2*resolution.y)
			adjustment.y = 2*resolution.y - bottom  - screen.y;
		if(screen.y - top < 0)
			adjustment.y = top - screen.y;
		this.move(adjustment.scale(-1.0f/scale_));
	}

}
