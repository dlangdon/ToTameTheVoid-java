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
	ForceOfNature[] forces;
	DelaunayLaneGenerator laneGenerator;
	MinimumSpanningTreeForce mstForce;
	
	boolean showHeatMap;
	boolean showPoints;
	boolean showTriangles;
	boolean showInitialEdges;
	boolean showMST;
	boolean showPrunnedEdges;
	
	/**
	 * For triangulation tests, this iterates one step at the time instead of running the whole thing.
	 * -1 is the initial value, then adds one point at the time, then cleans up and created edges.
	 */
	int pointCount;

	public GenerationVisualTest()
	{
		super("Generation Test");
		
		this.showHeatMap = true;
		this.showPoints = true;
		this.showTriangles = true;
		this.showInitialEdges = true;
		this.showPrunnedEdges = true;
		this.showMST = true;
		this.pointCount = -1;
		
		this.laneGenerator = new DelaunayLaneGenerator(0.2f);
		this.mstForce = new MinimumSpanningTreeForce();
		forces = new ForceOfNature[] {
				new SimpleBlobCreator(100, 100, 30, 4, 15),
				new SimplePointCreator(5, 50, 1.0f),
				this.laneGenerator,
				this.mstForce
		};
		
		setInitialData();
	}

	void setInitialData()
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
		// All visibility options.
		if(key == Input.KEY_Q)
			showHeatMap = !showHeatMap;
		if(key == Input.KEY_W)
			showPoints = !showPoints;
		if(key == Input.KEY_E)
			showTriangles = !showTriangles;
		if(key == Input.KEY_R)
			showInitialEdges = !showInitialEdges;
		if(key == Input.KEY_T)
			showMST = !showMST;
		if(key == Input.KEY_Y)
			showPrunnedEdges = !showPrunnedEdges;
		
		// Whole forces to unleash.
		if(c >= '1' && c <= '9')
		{
			if(c-'1' < forces.length)
				forces[c-'1'].unleash(nascent);
		}
		
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
				laneGenerator.end();
				laneGenerator.generateAllEdges();
			}
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
		
		if(laneGenerator.triangles != null && showTriangles)
		{
			Vector2f disp = new Vector2f(50, 50);
			int size = nascent.points.size();
			for(Triangle t: laneGenerator.triangles)
			{
				if(t.v1 >= size || t.v2 >= size || t.v3 >= size )
					continue;
				
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
				g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.1f));
				g.fillOval(center.x-r, center.y-r, 2*r, 2*r);
			}
		}
		
		if(nascent.initialLanes != null && showInitialEdges)
		{
			g.setColor(Color.cyan);
			for(Edge l : nascent.initialLanes)
			{
				Vector2f from = nascent.points.get(l.v1);
				Vector2f to = nascent.points.get(l.v2);
				g.drawLine(50+from.x*4, 50+from.y*4, 50+to.x*4, 50+to.y*4);
			}
		}
		
		if(nascent.prunedLanes != null && showPrunnedEdges)
		{
			g.setColor(Color.cyan);
			for(Edge l : nascent.prunedLanes)
			{
				Vector2f from = nascent.points.get(l.v1);
				Vector2f to = nascent.points.get(l.v2);
				g.drawLine(50+from.x*4, 50+from.y*4, 50+to.x*4, 50+to.y*4);
			}
		}
		
		if(mstForce.mst != null && showMST)
		{
			g.setColor(Color.red);
			for(Edge l : mstForce.mst)
			{
				Vector2f from = nascent.points.get(l.v1);
				Vector2f to = nascent.points.get(l.v2);
				g.drawLine(50+from.x*4, 50+from.y*4, 50+to.x*4, 50+to.y*4);
			}
		}
		
		if(nascent.points != null && showPoints)
		{
			g.setColor(Color.red);
			for(int p=0; p<nascent.points.size(); p++)
				if(pointCount < 0 || p < pointCount)
					g.fillOval(50+nascent.points.get(p).x*4-2, 50+nascent.points.get(p).y*4-2, 5, 5);
		}
		
		// Paint configuration feedback
		g.setColor(Color.red);
		g.drawRect(49, 49, 402, 402);
		g.setColor(Color.white);
		if(showHeatMap)
			g.drawString("HM", 10, 30);
		if(showPoints)
			g.drawString("PT", 10, 50);
		if(showTriangles)
			g.drawString("TR", 10, 70);
		if(showInitialEdges)
			g.drawString("IE", 10, 90);
		if(showMST)
			g.drawString("MST", 10, 110);
		if(showPrunnedEdges)
			g.drawString("PE", 10, 130);
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
