/**
 *
 */
package com.github.dlangdon.galaxy.generation;

import com.github.dlangdon.galaxy.generation.DelaunayLaneGenerator.Triangle;
import com.github.dlangdon.galaxy.generation.NascentGalaxy.Edge;

import java.util.ArrayList;
import java.util.HashSet;

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
	private static Color[] palette = new Color[]{
		new Color(210, 49, 93),
		new Color(247, 200, 8),
		new Color(34, 181, 191),
		new Color(135, 103, 166),
		new Color(233, 136, 19),
		new Color(136, 193, 52)
	};

	NascentGalaxy nascent;
	ForceOfNature[] forces;
	DelaunayLaneGenerator laneGenerator;
	MinimumSpanningTreeForce mstForce;
	StartingLocationFinder placer;

	boolean showHeatMap;
	boolean showPoints;
	boolean showTriangles;
	boolean showInitialEdges;
	boolean showMST;
	boolean showPrunnedEdges;
	boolean showStaringLocations;

	int currentForce;
	boolean interactive;

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
		this.showStaringLocations = true;
		this.pointCount = -1;
		this.currentForce = -1;
		this.interactive = false;

		nascent = new NascentGalaxy(100, 100, 1.0f);
		this.laneGenerator = new DelaunayLaneGenerator(0.15f);
		this.mstForce = new MinimumSpanningTreeForce();
		this.placer = new StartingLocationFinder(5, 15);
		forces = new ForceOfNature[] {
				new SimpleBlobCreator(2, 14, 30),
				new SimplePointCreator(5, 150),
				this.laneGenerator,
				this.mstForce,
				this.placer
		};

		setInitialData();
	}

	void setInitialData()
	{
		nascent.points = new ArrayList<Vector2f>();
		nascent.points.add(new Vector2f(10, 50));
		nascent.points.add(new Vector2f(30, 10));
		nascent.points.add(new Vector2f(35, 90));
		nascent.points.add(new Vector2f(70, 10));
		nascent.points.add(new Vector2f(65, 90));
		nascent.points.add(new Vector2f(90, 50));

		laneGenerator.triangles = new ArrayList<Triangle>();
		laneGenerator.triangles.add(new Triangle(0, 1, 2, nascent.points));
		laneGenerator.triangles.add(new Triangle(3, 4, 5, nascent.points));

		nascent.prunedEdges = new HashSet<NascentGalaxy.Edge>();
		nascent.prunedEdges.add(new Edge(0, 1));
		nascent.prunedEdges.add(new Edge(0, 2));
		nascent.prunedEdges.add(new Edge(1, 2));
		nascent.prunedEdges.add(new Edge(1, 3));
		nascent.prunedEdges.add(new Edge(2, 4));
		nascent.prunedEdges.add(new Edge(3, 4));
		nascent.prunedEdges.add(new Edge(3, 5));
		nascent.prunedEdges.add(new Edge(4, 5));
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
		if(key == Input.KEY_U)
			showStaringLocations = !showStaringLocations;
		if(key == Input.KEY_BACK)
			interactive = !interactive;

		// Whole forces to unleash.
		if(c >= '1' && c <= '9')
		{
			int f = c-'1';
			if(f < forces.length)
			{
				currentForce = f;
				System.err.println("Running force: " + forces[f].toString());

				if(!interactive)
					forces[f].unleash(nascent);

				// Interactive mode for com.github.dlangdon.empire placement.
				else if(forces[currentForce] == placer)
				{
					try
					{
						placer.init(nascent);
						placer.updateGalaxy();
					}
					catch (Exception e)
					{
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}

				// Re-creating points has serious consequences on existing lanes.
				if(f == 1)
				{
					nascent.initialEdges = null;
					nascent.prunedEdges = null;
					laneGenerator.triangles = null;
					mstForce.mst = null;
					nascent.startingLocations = null;
				}
			}
		}

		// Enter controls steps for the delaunay generation.
		if(key == Input.KEY_ENTER)
		{
			if(interactive && forces[currentForce] == placer)
			{
				System.err.println("Runnint Empire Placement Step");
				if(!placer.step())
					interactive = false;
				placer.updateGalaxy();
			}
			if(forces[currentForce] == laneGenerator)
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

		if(nascent.initialEdges != null && showInitialEdges)
		{
			g.setColor(Color.cyan);
			for(Edge l : nascent.initialEdges)
			{
				Vector2f from = nascent.points.get(l.v1);
				Vector2f to = nascent.points.get(l.v2);
				g.drawLine(50+from.x*4, 50+from.y*4, 50+to.x*4, 50+to.y*4);
			}
		}

		if(nascent.prunedEdges != null && showPrunnedEdges)
		{
			g.setColor(Color.gray);
			for(Edge l : nascent.prunedEdges)
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

		if(nascent.startingLocations != null && showStaringLocations)
		{
			// Repaint all points with the given color.
			for(int p=0; p<nascent.points.size(); p++)
			{
				int cluster = placer.medoids.indexOf(placer.nodes.get(p).medoid);
				g.setColor(cluster < 0 ? Color.gray : palette[cluster]);
				g.fillOval(50+nascent.points.get(p).x*4-2, 50+nascent.points.get(p).y*4-2, 5, 5);
			}

			// Paint starting locations bigger
			for(int s=0; s<nascent.startingLocations.size(); s++)
			{
				g.setColor(palette[s]);
				Vector2f v = nascent.points.get(nascent.startingLocations.get(s));
				g.fillRect(50+v.x*4-4, 50+v.y*4-4, 9, 9);
			}

		}

		// Paint configuration feedback
		g.setColor(Color.red);
		g.drawString("Current stage: " + (currentForce+1) + (interactive ? "(INTERACTIVE MODE)" : ""), 100, 10);

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
		if(showStaringLocations)
			g.drawString("SL", 10, 150);
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
