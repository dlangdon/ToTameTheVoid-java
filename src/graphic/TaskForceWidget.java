package graphic;

import java.util.Map.Entry;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.Design;
import state.TaskForce;

public class TaskForceWidget
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
	
	private int coordToIndex(Vector2f vector)
	{
		return -1;
	}
}
