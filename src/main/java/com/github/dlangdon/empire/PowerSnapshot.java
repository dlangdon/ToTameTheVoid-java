/**
 *
 */
package com.github.dlangdon.empire;

import java.util.ArrayList;
import java.util.HashMap;


import com.github.dlangdon.simulation.Simulator;

/**
 * Stores the power balance of all empires across each turn.
 * It can plot a graph
 * @author Daniel Langdon
 */
public class PowerSnapshot
{
	/**
	 * Add more when newer modules are created. Eventually these can be registered by other modules instead of hardcoded.
	 */
	enum Category { MILITARY, ECONOMY, STARS, TOTAL };
	private static HashMap<Category, Double> maxSeen = new HashMap<PowerSnapshot.Category, Double>();
	private static ArrayList<PowerSnapshot> snapshots = new ArrayList<PowerSnapshot>();

	private double scores[][];

	/**
	 *
	 */
	public PowerSnapshot()
	{
		scores = new double[Category.values().length][Empire.all().size()];
	}

	public static void addValue(Empire e, Category c, double value)
	{
		int turn = Simulator.instance().getTurnCount();
		while(snapshots.size() <= turn)
			snapshots.add(new PowerSnapshot());
		PowerSnapshot s = snapshots.get(turn);

		s.scores[c.ordinal()][Empire.all().indexOf(e)] = value;
		if(value > maxSeen.get(c))
			maxSeen.put(c, value);
	}

	public static double getScore(int turn, Empire e, Category c)
	{
		return snapshots.get(turn).scores[c.ordinal()][Empire.all().indexOf(e)];
	}

	public static double getScore(Empire e, Category c)
	{
		return getScore(Simulator.instance().getTurnCount(),  e, c);
	}
}
