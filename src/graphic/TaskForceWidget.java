package graphic;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.TaskForce;

public class TaskForceWidget implements UIListener
{
// Internals ==========================================================================================================	
	private TaskForce force;
	private Image[] backgrounds;
	private int[][] bckDeltas;

// Public Methods =====================================================================================================
	TaskForceWidget() throws SlickException
	{
		this.force = null;
		backgrounds = new Image[] 
			{
				new Image("resources/fleetBase.png"),
				new Image("resources/fleetExt1.png"),
				new Image("resources/fleetExt2.png"),
				new Image("resources/fleetExt3.png"),
				new Image("resources/fleetExt4.png")
			};
		
		bckDeltas = new int[][]
			{
				{	-74,	-60,	-121, -168,	-108 },
				{	-119,	-121,	-105,	-129,	-169 }
			};
	
	}

	void showForce(TaskForce force)
	{
		this.force = force;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(force == null)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, force.position());

		// Decide how many segments to show.
		int numStacks = force.stacks().size();
		for(int i=0; i<=numStacks/4 && i<5; i++)
			backgrounds[i].draw(bckDeltas[0][i], bckDeltas[1][i]);

		// Paint the icons and numbers.
		// FIXME for now just paint the locations, ignore actual stacks.
//		for(Entry<Design, Integer> entry : force.stacks().entrySet())
//		{
//			
//		}
		
		for(int i=0; i<numStacks; i++)
		{
			Vector2f pos = indexToCenterCoord(i);
			Render.normal.drawString(pos.x, pos.y, "" + i);
			g.fillOval(pos.x-1, pos.y-1, 3, 3);
		}
		
		// Paint the description, if any.
		

		
		g.popTransform();
	}
	
	private Vector2f indexToCenterCoord(int index)
	{
		// The widget supports a maximum of 23 different stacks, which should be more than enough.
		if(index > 22)
			return null;
		
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
			float angle = -(index-12)*10.0f - 180.0f;
			if(index%2 == 1)
				angle = -angle + 10.0f;

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
		if(force == null)
			return false;
		
		// Get the index.
		Vector2f local = new Vector2f(x, y).sub(Camera.instance().worldToScreen(force.position()));
		int index = coordToIndex(local);
		System.out.println("Click! index=" + index);
		if(index == -10)
			return false;
		
		// Process if it's a button.
		if(index < 0)
		{
			
		}
		
		// Process if its a stack.
		else
		{
			
		}
		
		return true;
	}
}
