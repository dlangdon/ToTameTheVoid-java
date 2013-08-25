package graphic;

import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.HQ;
import state.HQ.QueuedUnit;
import state.Unit;

public class HQWidget implements UIListener
{
// Internals ==========================================================================================================	
	private HQ hq;
	private Image[] backgrounds;
	private int hoverIndex;
	private int numSteps;

// Public Methods =====================================================================================================
	HQWidget() throws SlickException
	{
		this.hq = null;
		this.hoverIndex = -1;
		this.numSteps = 6;
		backgrounds = new Image[] 
			{
				new Image("resources/fleetBase.png"),
				new Image("resources/fleetExt1.png"),
				new Image("resources/fleetExt2.png"),
				new Image("resources/fleetExt3.png"),
			};
	}

	/**
	 * Sets the task fleet to be displayed by this
	 * @param hq
	 */
	void showHQ(HQ hq)
	{
		this.hq = hq;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(hq == null)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, hq.colony().location().getPos());
		
		// Paint all backgrounds. TODO mix with single resource set or create single static background.
		backgrounds[0].draw(-74, -119);
		backgrounds[1].draw(-60, -121);
		backgrounds[2].draw(-121, -105);

		List<QueuedUnit> queue = hq.queue();
		if(!queue.isEmpty())
			backgrounds[3].draw(-168, -129);

		// Display available units to build
		List<Unit> options = hq.availableUnits();
		for(int i=0; i<options.size(); i++)
		{
			Vector2f pos = indexToCenterCoord(i);
			options.get(i).image().draw(pos.x-15, pos.y-15);
			// Check if we also display the local information.

			if(hoverIndex == i)
				Render.titles.drawString(120, -100, options.get(i).name());
		}
		
		// Display current queue (with numbers)
		for(int i=0; i<5; i++)
		{
			Vector2f pos = indexToCenterCoord(i);
			if(i<queue.size())
			{
				QueuedUnit qu = queue.get(i);
				qu.design.image().draw(pos.x-15, pos.y-15);
				
				// Calculate location and draw the count for the stack.
				g.setColor(Color.orange);
				float length = pos.length();
				String number = Integer.toString((int)qu.queued);
				pos.normalise().scale(length + 10.0f);
				g.fillRect(
							pos.x - Render.normal.getWidth(number)/2,
							pos.y - Render.normal.getHeight()/2,
							Render.normal.getWidth(number),
							Render.normal.getHeight());
				Render.normal.drawString(
							pos.x - Render.normal.getWidth(number)/2,
							pos.y - Render.normal.getHeight()/2,
							number, Color.black);

			}
			else if(!queue.isEmpty())
			{
				String number = Integer.toString(i+1);
				Render.normal.drawString(
						pos.x - Render.normal.getWidth(number)/2,
						pos.y - Render.normal.getHeight()/2,
						number, Color.black);
			}
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
		// Determine first 12 segments.
		if(index < 12)
		{
			float angle = index*15.0f;
			if(index%2 == 0)
				angle = -angle - 15.0f;
			
			return new Vector2f(angle).scale(98.5f);
		}
		else
		{
			float angle = -(index-12)*10.0f - 200.0f;
			return new Vector2f(angle).scale(145.5f);
		}
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
		else if(123 < radius && radius < 168 )
		{
			// Second circle
			int aux = (int)(angle - 10) / 20;
			if(aux > 2)
			{
				if(aux < 9)
					return 28 - aux*2;
				else if(aux < 14)
					return aux*2 - 5;
			}
		}

		return -10;
	}

	@Override
	public boolean screenCLick(float x, float y, int button)
	{
		// Check if visible.
		if(hq == null)
			return false;

		List<QueuedUnit> queue = hq.queue();
		List<Unit> options = hq.availableUnits();
		
		// Get the index.
		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(hq.colony().location().getPos()));
		int index = coordToIndex(local);
		if(index >= 12)		// Queue
		{
			// TODO Not sure what should happen here...if anything
			if(index > queue.size())
				return true;
		}
		else if(index >= 0) 	// Slot
		{
			if(index > options.size())
				return false;
			
			int i = 0;
			while(queue.get(i).design != options.get(index) && i<= queue.size())
				i++;

			QueuedUnit unit = null;
			if(i > 4)
			{
				System.out.println("Can put more than 5 units in the queue");
				return true;
			}
			else if(i > queue.size())
			{
				unit = new QueuedUnit();
				unit.design = options.get(index);
				unit.queued = 0;
				queue.add(new QueuedUnit());
			}
			else
				unit = queue.get(i);

			// Add or remove from this queued unit.
			if(button == 0)
				unit.queued++;
			else if(button == 1)
			{
				if(unit.queued < 2)
					queue.remove(i);
				else
					unit.queued--;
			}
		}
		else if(index >= -5)	// Button
		{
			// TODO
		}
		else
			return false;

		return true;
	}
	
	public void mouseMoved(int oldx, int oldy, int newx, int newy) 
	{
		// Check if we are active.
		if(hq == null)
			return;
		
		// Get the index to display.
		Vector2f local = new Vector2f(newx, newy).sub(Camera.instance().worldToScreen(hq.colony().location().getPos()));
		hoverIndex = coordToIndex(local);
	}

}
