/**
 *
 */
package com.github.dlangdon.galaxy.structure;

import com.github.dlangdon.graphic.Camera;
import com.github.dlangdon.graphic.Selection;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import com.github.dlangdon.empire.Empire;
import com.github.dlangdon.state.Star;
import com.github.dlangdon.ui.UIListener;

/**
 * Interface for all objects that will be orbiting a star.
 * These orbiters will need to be able to display themselves in the correct location around their star, in the correct priority.
 * Also, these icons might be selected in order to produce other actions.
 * @author Daniel Langdon
 */
public abstract class Placeable implements UIListener
{
	private Place location_;

	protected Placeable(Place location)
	{
		location_ = location;
		location_.arrive(this);
	}

	/**
	 * @return the icon that represents this object on the screen.
	 */
	public abstract Image icon();

	/**
	 * A priority sorts the objects to be displayed around a star.
	 * <br>Some guidelines:
	 * <ul>	<li>Use the most significant digits to order objects by type.</li>
	 * 		<li>Equal objects owned by different empires should have a consistent order, use the index for the com.github.dlangdon.empire list to determine the least-signigicant digit.</li>
	 * 		<li>Objects owned by the com.github.dlangdon.empire who claims the star should have higher priority. Use a 9 instead of the normal com.github.dlangdon.empire order.</li>
	 *	</ul>
	 * @return the priority in terms of how objects in orbit are ordered.
	 */
	public abstract int priority();

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		if(location_ instanceof Star)
		{
			Star location_ = (Star)this.location_;
			Vector2f screen = new Vector2f(25.0f, 0.0f);

			// Orbiting the star. In this case, each fleet is separated by a 30 degree angle.
			screen.setTheta(-30 * location_.indexOf(this) - 30);
			screen.add(Camera.instance().worldToScreen(location_.getPos()));

			// Compare against mouse screen position.
			Vector2f local = new Vector2f(x, y).sub(screen);
			return (local.x * local.x <= 25 && local.y * local.y <= 25);
		}
		return false;
	}

	public void render(GameContainer gc, Graphics g)
	{
		if(location_ instanceof Star)
		{
			Star location_ = (Star)this.location_;

			Color color = (location_.owner() != null) ? place().owner().color() : Color.white;
			if(Selection.is(this))
			{
				float alpha = 1.0f - 1.2f * Math.abs((System.currentTimeMillis() % 1500) / 1500.0f - 0.5f);
				color = new Color(color.r, color.g, color.b, alpha);
			}

			// Paint orbiting the star. In this case, each fleet is separated by a 30 degree angle.
			Vector2f pos = new Vector2f(30.0f, 0.0f);
			pos.setTheta(-30 * location_.indexOf(this) - 30);
			drawIcon(location_.getPos(), g, pos, color);
		}
	}

	protected void drawIcon(Vector2f world, Graphics g, Vector2f screenDisp, Color color)
	{
		Camera.instance().pushLocalTransformation(g, world);
//		g.fillRect(screenDisp.x-4, screenDisp.y-4, 9, 9);
		icon().draw(screenDisp.x-9, screenDisp.y-9, color);
		g.popTransform();
	}

	public Place place()
	{
		return location_;
	}

	/**
	 * @return The place, casted to a star. This is useful when the caller has given guarantees about the place.
	 */
	public Star star()
	{
		return (Star) location_;
	}

	/**
	 * Moves a placeable from one place to another. Assumes that the move has been correctly validated before.
	 */
	protected void setPlace(Place p)
	{
		location_.leave(this);
		location_ = p;
		location_.arrive(this);
	}

	/**
	 * Most things are owned by whoever claims the location they are in.
	 */
	public Empire owner()
	{
		return place().owner();
	}

}
