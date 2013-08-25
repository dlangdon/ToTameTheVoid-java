/**
 * 
 */
package state;

import graphic.Camera;
import graphic.UIListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

/**
 * Interface for all objects that will be orbiting a star.
 * These orbiters will need to be able to display themselves in the correct location around their star, in the correct priority.
 * Also, these icons might be selected in order to produce other actions.
 * @author Daniel Langdon
 */
public abstract class Orbiter implements UIListener
{
	protected Star location_;
	
	protected Orbiter(Star location)
	{
		location_ = location;
	}
	
	/**
	 * @return the icon that represents this object on the screen.
	 */
	public abstract Image icon();
	
	/**
	 * A priority sorts the objects to be displayed around a star.
	 * <br>Some guidelines:
	 * <ul>	<li>Use the most significant digits to order objects by type.</li>	
	 * 		<li>Equal objects owned by different empires should have a consistent order, use the index for the empire list to determine the least-signigicant digit.</li>
	 * 		<li>Objects owned by the empire who claims the star should have higher priority. Use a 9 instead of the normal empire order.</li>
	 *	</ul>		
	 * @return the priority in terms of how objects in orbit are ordered.
	 */
	public abstract int priority();
	
	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		Vector2f screen = new Vector2f(20.0f, 0.0f);

		// Force orbiting the star. In this case, each fleet is separated by a 30 degree angle.
		screen.setTheta(-30 * location_.getDock(this) - 30);
		screen.add(Camera.instance().worldToScreen(location_.getPos()));
			
		// Compare against mouse screen position.
		Vector2f local = new Vector2f(x, y).sub(screen);
		return (local.x * local.x <= 25 && local.y * local.y <= 25);
	}
	
	public void render(GameContainer gc, Graphics g, int flags)
	{
		Color color = Color.white;
		if(location_.colony() != null)
			color = location_.colony().owner().color();
		
		// Paint orbiting the star. In this case, each fleet is separated by a 30 degree angle.
		Vector2f pos = new Vector2f(20.0f, 0.0f);
		pos.setTheta(-30 * location_.getDock(this) - 30);
		drawIcon(location_.getPos(), g, pos, color);
	}
	
	protected void drawIcon(Vector2f world, Graphics g, Vector2f screenDisp, Color color)
	{
		Camera.instance().pushLocalTransformation(g, world);
//		g.fillRect(screenDisp.x-4, screenDisp.y-4, 9, 9);
		icon().draw(screenDisp.x-9, screenDisp.y-9, color);
		g.popTransform();
	}
	
	public Star location()
	{
		return location_;
	}

}
