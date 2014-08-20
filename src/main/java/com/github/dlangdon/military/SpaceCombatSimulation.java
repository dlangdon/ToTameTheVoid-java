/**
 *
 */
package com.github.dlangdon.military;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.github.dlangdon.empire.Empire;
import com.github.dlangdon.state.Fleet;
import com.github.dlangdon.state.Unit;
import com.github.dlangdon.state.UnitStack;

/**
 * A fairly simple combat com.github.dlangdon.simulation, that can be overloaded by other simulations.
 * @author Daniel Langdon
 */
public class SpaceCombatSimulation
{
	/**
	 * A quick structure to store targets for attack.
	 */
	protected class Attack
	{
		int fleet;
		Unit stack;
		int kills;

		protected Attack(int fleet, Unit stack)
		{
			this.fleet = fleet;
			this.stack = stack;
		}

		protected void addDamage(int damage)
		{
			kills += damage;
		}

		protected void applyEffects()
		{
			inCombat[fleet].takeDamage(stack, 9999, kills, false);
		}
	}

	protected Fleet[] inCombat;					// All fleets in combat.
	protected boolean[] noTargets;				// If a specific fleet should keep shooting.

	/**
	 * Constructor.
	 */
	public SpaceCombatSimulation(List<Fleet> fleets)
	{
		inCombat = new Fleet[fleets.size()];
		inCombat = fleets.toArray(inCombat);
		noTargets = new boolean[fleets.size()];
	}

	/**
	 * @return True if no mutually hostile forces remain.
	 */
	public boolean finished()
	{
		for(boolean t : noTargets)
			if(t == false)
				return false;
		return true;
	}

	/**
	 * Runs the com.github.dlangdon.simulation.
	 * In this case, each 3 ships randomly kill a ship in the opposite fleet.
	 */
	public void run()
	{
		while(!finished())
		{
			// Pre-calculate the damage caused by a fleet before damage is applied.
			float[] kills = new float[inCombat.length];
			for(int i=0; i<inCombat.length; i++)
			{
				for(Entry<Unit, UnitStack> entry : inCombat[i].stacks().entrySet())
				{
					kills[i] += entry.getValue().quantity() / 3.0f;
				}
				kills[i] = (float) Math.ceil(kills[i]);
			}

			for(int i=0; i<inCombat.length; i++)
			{
				// If there were no target, they will not appear from nowhere.
				if(noTargets[i])
					continue;

				// Collect potential targets to apply the damage to.
				ArrayList<Attack> attacks = new ArrayList<Attack>();
				for(int j=0; j<inCombat.length; j++)
				{
					double trust = inCombat[i].owner().reciprocalTrust(inCombat[j].owner());
					if(trust < Empire.CEASE_FIRE)
					{
						// Distribute attacks evenly over all enemy stacks.
						for(Unit d : inCombat[j].stacks().keySet())
							attacks.add(new Attack(j, d));
					}
				}

				// Check if there is anything to hit and I can actually hit it.
				if(kills[i] <= 0 || attacks.isEmpty())
				{
					noTargets[i] = true;
					continue;
				}

				// Apply the damage evenly.
				for(Attack a : attacks)
				{
					UnitStack s = inCombat[a.fleet].stacks().get(a.stack);
					if(s != null)
					{
						int trueKills = (int) (kills[i] > s.quantity() ? s.quantity() : kills[i]);
						kills[i] -= trueKills;
						a.addDamage(trueKills);
						a.applyEffects();
						if(kills[i] <= 0)
							break;
					}
				}
			}
		}
	}
}
