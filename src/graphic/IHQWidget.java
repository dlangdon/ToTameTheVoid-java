package graphic;

import java.util.HashMap;
import java.util.Map.Entry;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.Fleet;
import state.IHQSlot;
import state.ImperialHQ;
import state.Unit;
import state.UnitStack;

public class IHQWidget implements UIListener
{
// Internals ==========================================================================================================	
	private ImperialHQ ihq;
	private Image background;

// Public Methods =====================================================================================================
	IHQWidget() throws SlickException
	{
		this.ihq = null;
		background = new Image("resources/IHQBase.png");
	}

	/**
	 * Sets the task fleet to be displayed by this
	 * @param fleet
	 */
	void showIHQ(ImperialHQ ihq)
	{
		this.ihq = ihq;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(ihq == null)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, ihq.getRelocation().get(0).getPos());
		background.draw(-84, -119);
		
		// Paint the icons and numbers.
		int i=-1;
		for(IHQSlot slot : ihq.slots())
		{
			if(slot == null)
				break;
			i++;
			
			// Draw the icon.
			Vector2f pos = indexToCenterCoord(i);
			slot.icon().draw(pos.x-9, pos.y-9);
			
			// Calculate location and draw the count for the stack.
			pos.normalise().scale(150.0f);
			Render.titles.drawString(
						pos.x,
						pos.y - Render.titles.getHeight(),
						slot.inConstruction().name(), Color.white);
			Render.normal.drawString(
					pos.x,
					pos.y,
					"30/turn", Color.white);

		}
		
		g.popTransform();
	}
	
	/**
	 * Translates a placeholder index for a stack in this widget to a local coordinates (around widget's center).
	 * @param index Index to translate, must be in the range 0-22.
	 * @return A vector with the local coordinates, or null if not
	 */
	private Vector2f indexToCenterCoord(int index)
	{
		float angle = index*15.0f;
		if(index%2 == 0)
			angle = -angle - 15.0f;
		
		return new Vector2f(angle).scale(98.5f);
	}
	
	/**
	 * Finds the potential element in the UI that corresponds to a particular coordinate.
	 * This method does not consider what parts of the widget are current being displayed or their content, just placejolder locations.
	 * @param vector A 2-dimensional vector of screen coordinates centered on the current position.
	 * @return An index between 0 and 22 that corresponds to the slot in the widget that matches that point. OR a code between -1 to -5 to indicate one of the available buttons (top to bottom) OR -10 if there is no match whatsoever.
	 */
	private int coordToIndex(Vector2f vector)
	{
		double angle = vector.getTheta();
		double radius = vector.length();
		
		if(54 < radius && radius < 74 )
		{
			// Buttons
			if(angle >= 310)
				return -1 - (int)((angle - 310.0) / 20.0);
			
			if(angle <= 50)
				return -1 - (int)((angle + 50) / 20.0);
		}
		if(76 < radius && radius < 121 )
		{
			// First circle, all of it works
			int aux = (int)(360 - angle) / 30;
			if(aux < 6)
				return aux*2;
			else
				return 23 - aux*2; 
		}
		return -10;
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
//		// Check if visible.
//		if(ihq == null)
//			return false;
//		
//		// Get the index.
//		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(fleet.position()));
//		int index = coordToIndex(local);
//		if(index == -10 || index >= cache.length)
//			return false;
//		
//		// Process if it's a button.
//		if(index < 0)
//		{
//			// TODO
//		}
//		
//		// Process if its a stack.
//		else
//		{
//			// This calculation may seem rather convoluted and the % operator may sound like a better idea, but this behavior is rather rare. 
//			// If we are close to the maximum, we want to go to 12 before going pass 12. Example to avoid: 0, 3, 6, 9, 12, 2, 5, 8, 11...
//			float step = Math.max(1.0f * cache[index].max / numSteps, 1.0f);
//			if(button == 0)
//			{
//				if(cache[index].selected == cache[index].max)
//					cache[index].selected = 0;
//				else
//					cache[index].selected = Math.min(cache[index].selected + step, cache[index].max);
//			}
//			else if(button == 1)
//			{
//				if(cache[index].selected < 1.0f)
//					cache[index].selected = cache[index].max;
//				else
//					cache[index].selected = Math.max(cache[index].selected - step, 0.0f);
//			}
//			System.out.println(cache[index].selected);
//		}
//		
		return true;
	}
	
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
//		// Check if we are active.
//		if(ihq == null)
//			return;
//		
//		// Get the index to display.
//		Vector2f local = new Vector2f(newx, newy).sub(Camera.instance().worldToScreen(fleet.position()));
//		hoverIndex = coordToIndex(local);
	}
}
