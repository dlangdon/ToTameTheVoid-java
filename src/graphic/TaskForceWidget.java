package graphic;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

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

		for(int i=0; i<backgrounds.length; i++)
			backgrounds[i].draw(bckDeltas[0][i], bckDeltas[1][i]);
		
		g.popTransform();
	}
}
