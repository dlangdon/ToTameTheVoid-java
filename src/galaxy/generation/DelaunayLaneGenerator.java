/**
 * 
 */
package galaxy.generation;

import galaxy.generation.NascentGalaxy.Lane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.newdawn.slick.geom.Vector2f;

/**
 * @author Daniel Langdon
 */
public class DelaunayLaneGenerator implements ForceOfNature
{
	private final static double EPSILON = 0.00001;
	
	static class Triangle
	{
		int v1;
		int v2;
		int v3;
		Vector2f center;
		float sqrRadius;
		float areaVsCircle;

		Triangle(int V1, int V2, int V3, List<Vector2f> points)
		{
			assert(V1 < V2);
			assert(V1 < V3);

			// Asigno los vertices.
			v1 = V1;
			v2 = V2;
			v3 = V3;
			center = new Vector2f();

			// calculo el circumcirculo.
			float x0 = points.get(v1).getX();
			float y0 = points.get(v1).getY();

			float x1 = points.get(v2).getX();
			float y1 = points.get(v2).getY();

			float x2 = points.get(v3).getX();
			float y2 = points.get(v3).getY();

			float y10 = y1 - y0;
			float y21 = y2 - y1;

			boolean b21zero = (y21 > -EPSILON) && (y21 < EPSILON);
			if (y10 > -EPSILON && y10 < EPSILON)
			{
				if (b21zero)	// All three vertices are on one horizontal line.
				{
					if (x1 > x0)
					{
						if (x2 > x1)
							x1 = x2;
					}
					else
					{
						if (x2 < x0)
							x0 = x2;
					}
					center.set((x0 + x1) * .5F, y0);
				}
				else	// m_Vertices[0] and m_Vertices[1] are on one horizontal line.
				{
					float m1 = - (x2 - x1) / y21;

					float mx1 = (x1 + x2) * .5F;
					float my1 = (y1 + y2) * .5F;

					center.set((x0 + x1) * .5F, m1 * (center.getX() - mx1) + my1);
				}
			}
			else if (b21zero)	// m_Vertices[1] and m_Vertices[2] are on one horizontal line.
			{
				float m0 = - (x1 - x0) / y10;

				float mx0 = (x0 + x1) * .5F;
				float my0 = (y0 + y1) * .5F;

				center.set((x1 + x2) * .5F, m0 * (center.getX() - mx0) + my0);
			}
			else	// 'Common' cases, no multiple vertices are on one horizontal line.
			{
				float m0 = - (x1 - x0) / y10;
				float m1 = - (x2 - x1) / y21;

				float mx0 = (x0 + x1) * .5F;
				float my0 = (y0 + y1) * .5F;

				float mx1 = (x1 + x2) * .5F;
				float my1 = (y1 + y2) * .5F;

				center.set((m0 * mx0 - m1 * mx1 + my1 - my0) / (m0 - m1), m0 * (center.getX() - mx0) + my0);
			}

			float dx = x0 - center.getX();
			float dy = y0 - center.getY();
			sqrRadius = dx * dx + dy * dy;	// the radius of the circumcircle, squared

			// Calculamos el area del triangulo proporcional al circumcirculo.

			// Llevamos el vertice v1 a (0,0), actualizando los otros 2.
			Vector2f B = new Vector2f(points.get(v2)).sub(points.get(v1));
			Vector2f C = new Vector2f(points.get(v3)).sub(points.get(v1));

			// Formula de area por coordenadas para triangulo con un vertice en (0,0).
			float area = Math.abs(B.getX() * C.getY() - C.getX() * B.getY()) / 2;
			areaVsCircle = area / (3.14159f * sqrRadius);
		}

		boolean isInCircumcircle(Vector2f p)
		{
			Vector2f dif = new Vector2f(center).sub(p);
			return (dif.getX()*dif.getX() + dif.getY()*dif.getY() <= sqrRadius);
		}

	};

	List<Triangle> triangles;
	private float prunningRatio;
	NascentGalaxy nascent;
	
	/**
	 * Creates a bunch of points.
	 * @param exclusionRadius
	 * @param maxFailures
	 * @param explosionFactor The random coordinates created relate to the heatmap size, so this multiplier is used to get intended world coordinates. 
	 */
	public DelaunayLaneGenerator(float f)
	{
		this.prunningRatio = f;
	}
	
	/* (non-Javadoc)
	 * @see galaxy.generation.ForceOfNature#apply(galaxy.generation.NascentGalaxy)
	 */
	@Override
	public boolean apply(NascentGalaxy nascent)
	{
		if(nascent.points == null)
			return false;
		this.nascent = nascent;
		
		generateTriangles();
		generateAllEdges();
		
		return true;
	}

	void generateTriangles()
	{
		// Creamos el mega triangulo!
		// Este siempre tiene que abarcar cuaulquier punto que pudieramos querer colocar, pero en la practica no esperamos crear algo mayor a 100 o 1000 de lado.
		int size = nascent.points.size();
		nascent.points.add(new Vector2f(-100000.0f, -100000.0f));
		nascent.points.add(new Vector2f(100000.0f, -100000.0f));
		nascent.points.add(new Vector2f(0.0f, 100000.0f));
		
		triangles = new ArrayList<Triangle>();
		Triangle megaTriangle = new Triangle(size, size+1, size+2, nascent.points);
		triangles.add(megaTriangle);

		// Vamos agragando los vertices uno a uno. (Excluimos los 3 ultimos, que corresponden al megaTriangle)
		for (int p = 0; p < nascent.points.size()-3 ; p++)
		{
			// Encontramos los triangulos cuyos circumcirculos incluyen al punto.
			// Aquellos almacenamos sus vertices pero los eliminamos.
			Iterator<Triangle> i = triangles.iterator();
			HashMap<Lane, Integer> edges = new HashMap<Lane, Integer>();
			while (i.hasNext())
			{
				Triangle t = i.next();

				if (t.isInCircumcircle(nascent.points.get(p)))
				{
					Lane l1 = nascent.new Lane(t.v1, t.v2);
					Integer val1 = edges.get(l1);
					edges.put(l1, val1 == null ? 1 : val1+1);
					
					Lane l2 = nascent.new Lane(t.v1, t.v3);
					Integer val2 = edges.get(l2);
					edges.put(l2, val2 == null ? 1 : val2+1);

					Lane l3 = nascent.new Lane(t.v2, t.v3);
					Integer val3 = edges.get(l3);
					edges.put(l3, val3 == null ? 1 : val3+1);

					i.remove();
				}
			}

			// Creamos nuevos triangulos para las aristas no repetidas.
			for (Entry<Lane, Integer> j : edges.entrySet())
			{
				if(j.getValue() < 2)
				{
					// Ojo que por contrato los vertices deben ir en orden ascendente. (pero ya sabemos el orden de 2 vertices)
					if(p < j.getKey().v1)
						triangles.add(new Triangle(p, j.getKey().v1, j.getKey().v2, nascent.points));
					else if(p < j.getKey().v2)
						triangles.add(new Triangle(j.getKey().v1, p, j.getKey().v2, nascent.points));
					else
						triangles.add(new Triangle(j.getKey().v1, j.getKey().v2, p, nascent.points));
				}
			}
		}

		// Eliminamos el megaTriangle y creamos la lista de adjacencias.
		nascent.points.remove(size+2);
		nascent.points.remove(size+1);
		nascent.points.remove(size);
		
		System.out.println("Created " + triangles.size() + " triangles.");
	}

	void generateAllEdges()
	{
		nascent.initialLanes = new HashSet<Lane>();
		nascent.prunedLanes = new HashSet<Lane>();
		
		for(Triangle triangle : triangles)
		{
			addEdge(triangle.v1, triangle.v3, nascent.initialLanes);
			addEdge(triangle.v2, triangle.v3, nascent.initialLanes);
			addEdge(triangle.v1, triangle.v2, nascent.initialLanes);

			if(triangle.areaVsCircle > prunningRatio)
			{
				addEdge(triangle.v1, triangle.v3, nascent.prunedLanes);
				addEdge(triangle.v2, triangle.v3, nascent.prunedLanes);
				addEdge(triangle.v1, triangle.v2, nascent.prunedLanes);
			}
		}
		
		System.out.println("Created " + nascent.initialLanes.size() + " initial lanes.");
		System.out.println("Prunend down to " + nascent.prunedLanes.size() + " lanes.");
	}
	
	private void addEdge(int v1, int v2, Set<Lane> set)
	{
		if(v1 < nascent.points.size() && v2 < nascent.points.size())
			set.add(nascent.new Lane(v1, v2));
	}
}
