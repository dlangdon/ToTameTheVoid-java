/**
 * 
 */
package galaxy.generation;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class GenerationVisualTest implements Game
{
	NascentGalaxy nascent;

	public GenerationVisualTest()
	{
		super();
		nascent = new NascentGalaxy();

		// Configure pipeline
		nascent.addForce(new SimpleBlobCreator(100, 100, 30, 2, 12));
		nascent.runAllForces();
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#init(org.newdawn.slick.GameContainer)
	 */
	@Override
	public void init(GameContainer container) throws SlickException
	{
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#update(org.newdawn.slick.GameContainer, int)
	 */
	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#render(org.newdawn.slick.GameContainer, org.newdawn.slick.Graphics)
	 */
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException
	{
		if(nascent.bornStars != null)
		{
			// TODO paint stars. Should they be clickable and use the widget??
		}
		else if(nascent.prunedLanes != null)
		{
			
		}
		else if(nascent.initialLanes != null)
		{
			
		}
		else if(nascent.points != null)
		{
			
		}
		else if(nascent.heatmap != null)
		{
			g.setColor(Color.red);
			g.drawRect(49, 49, 402, 402);

			for(int i=0; i<nascent.heatmap.length ; i++)
			{
				float[] row = nascent.heatmap[i];
				for(int j=0; j<row.length ; j++)
				{
					g.setColor(new Color(row[j], row[j], row[j]));
					g.fillRect(50+i*4, 50+j*4, 4, 4);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#closeRequested()
	 */
	@Override
	public boolean closeRequested()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.newdawn.slick.Game#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return "Generation Test";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			GenerationVisualTest test = new GenerationVisualTest();
			AppGameContainer app = new AppGameContainer(test);
			app.setDisplayMode(500, 500, false);
			app.start();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

}
