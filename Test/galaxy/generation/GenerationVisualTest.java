/**
 * 
 */
package galaxy.generation;

import galaxy.generation.DelaunayLaneGenerator.Triangle;
import galaxy.generation.NascentGalaxy.Edge;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class GenerationVisualTest extends BasicGame
{
	NascentGalaxy nascent;
	int maxSteps;
	boolean showHeatMap;
	int pointCount;
	boolean laneDetails;
	DelaunayLaneGenerator laneGenerator;

	public GenerationVisualTest()
	{
		super("Generation Test");
		
		this.maxSteps = 0;
		this.showHeatMap = true;
		this.laneDetails = false;
		this.pointCount = -1;
		this.laneGenerator = new DelaunayLaneGenerator(0.5f);
		
//		runPipeline();
		runFixedPoints();
	}
	
	void runPipeline()
	{
		nascent = new NascentGalaxy();

		// Configure pipeline
		nascent.addForce(new SimpleBlobCreator(100, 100, 30, 4, 15));
		nascent.addForce(new SimplePointCreator(5,3, 1.0f));
		nascent.addForce(laneGenerator);
		
		if(maxSteps < nascent.forces.size())
				nascent.forces = nascent.forces.subList(0, maxSteps);
		
		if(!nascent.runAllForces())
			System.out.println("ERROR: Could not run all pipeline forces.");
	}

	void runFixedPoints()
	{
		nascent = new NascentGalaxy();
		nascent.points = new ArrayList<Vector2f>();
		nascent.points.add(new Vector2f(30, 70));
		nascent.points.add(new Vector2f(20, 30));
		nascent.points.add(new Vector2f(70, 90));
		nascent.points.add(new Vector2f(30, 30));

		laneGenerator.triangles = new ArrayList<Triangle>();
		laneGenerator.triangles.add(new Triangle(0, 1, 2, nascent.points));
		laneGenerator.triangles.add(new Triangle(1, 2, 3, nascent.points));
	}
	
	@Override
	public void keyPressed(int key, char c)
	{
		if(key == Input.KEY_H)
			showHeatMap = !showHeatMap;
		if(key == Input.KEY_L)
			laneDetails = !laneDetails;
		
		// Enter controls steps for the delaunay generation.
		if(key == Input.KEY_ENTER)
		{
			if(pointCount == -1)
			{	
				laneGenerator.init(nascent);
				pointCount++;
			}
			else if(pointCount < nascent.points.size()-3)
				laneGenerator.step(this.pointCount++);
			else if(pointCount == nascent.points.size()-3)
			{
//				laneGenerator.end();
				laneGenerator.generateAllEdges();
			}
		}
		
		// Re-run experiment
		if(c >= '0' && c <= '9')
		{
			maxSteps = c - '0';
			runPipeline();
		}
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
		if(nascent.heatmap != null && showHeatMap)
		{
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

		if(nascent.bornStars != null)
		{
			// TODO paint stars. Should they be clickable and use the widget??
		}
		else if(nascent.prunedLanes != null)
		{
			
		}
		
		if(laneGenerator.triangles != null)
		{
			Vector2f disp = new Vector2f(50, 50);
			for(Triangle t: laneGenerator.triangles)
			{
				Vector2f v1 = new Vector2f(nascent.points.get(t.v1)).scale(4.0f).add(disp);
				Vector2f v2 = new Vector2f(nascent.points.get(t.v2)).scale(4.0f).add(disp);
				Vector2f v3 = new Vector2f(nascent.points.get(t.v3)).scale(4.0f).add(disp);
				float r = (float) Math.sqrt(t.sqrRadius) * 4;
				Vector2f center = new Vector2f(t.center).scale(4.0f).add(disp);

				g.setColor(Color.cyan);
				g.drawLine(v1.x, v1.y, v2.x, v2.y);
				g.drawLine(v1.x, v1.y, v3.x, v3.y);
				g.drawLine(v3.x, v3.y, v2.x, v2.y);

				g.fillOval(center.x-1, center.y-1, 3, 3);
				g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.3f));
				g.fillOval(center.x-r, center.y-r, 2*r, 2*r);
			}
		}
		
		if(nascent.initialLanes != null)
		{
			g.setColor(Color.cyan);
			for(Edge l : nascent.initialLanes)
			{
				Vector2f from = nascent.points.get(l.v1);
				Vector2f to = nascent.points.get(l.v2);
				g.drawLine(50+from.x*4, 50+from.y*4, 50+to.x*4, 50+to.y*4);
			}
		}
		
		if(nascent.points != null)
		{
			g.setColor(Color.red);
			for(int p=0; p<nascent.points.size(); p++)
				if(pointCount < 0 || p < pointCount)
					g.fillOval(50+nascent.points.get(p).x*4-2, 50+nascent.points.get(p).y*4-2, 5, 5);
		}
		
		g.setColor(Color.red);
		g.drawRect(49, 49, 402, 402);
		g.setColor(Color.white);
		g.drawString("F"+maxSteps, 10, 30);
		if(showHeatMap)
			g.drawString("HM", 10, 50);
		
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
