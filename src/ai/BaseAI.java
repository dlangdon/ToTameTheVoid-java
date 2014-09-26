/**
 *
 */
package ai;

import ai.incentives.Incentive;
import empire.Empire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ai.incentives.Incentive.Area;

/**
 * @author Daniel Langdon
 */
public class BaseAI extends AI
{
   /**
    * An incentive to grow economy proportional to the average ROI on the colonies.
    * The score grows logarithmically and caps at 100 turns.
    */
   Incentive econROI()
   {
      float score = (float) Math.min( Math.log10(managed.getEconomy().avgROI() / 10 ), 1.0);
      return new Incentive("Our colonies have room to expand.", Area.GROW, score, 1);
   }

   /* General ideas:

      * Figure out if frontier easier than assess threat, but threat 0 if so and threat has to be computed anyway if frontier. ==> Compute frontier as part of threat.
      * Incentives placed on a particular star have an associated task, which can be generated immediately if needed.

      forall(stars)
         if(mine)
            -- compute threat (all neighbors)
            if(threat != 0)
               -- defend!

             if(!colony)
               -- colonize!
         else
            -- compute reward (resources, strategic position, defenses, owner, explored, etc)
    */

   static Incentive BASIC_CONTROL = new Incentive("We need room to expand.", Area.EXPAND, 1.0f, Integer.MAX_VALUE);


   protected BaseAI(Empire managed)
   {
      super(managed);
      addDefaultIncentives();
   }

   public static HashMap<Incentive.Area, List<Incentive>> incentives = new HashMap<>();

   @Override
   void run()
   {
      // Trust allocations

      // Unit allocation

      // Production allocation
   }


   void addDefaultIncentives()
   {


   }

   void allocateTrust()
   {

   }

   void allocateUnits()
   {

   }

   void allocateProduction()
   {

   }

   String dumpIncentives()
   {
      StringBuilder sb = new StringBuilder("Current incentives for " + managed.name() + ":\n");
      for(Map.Entry<Incentive.Area, List<Incentive>> entry : incentives.entrySet())
         for(Incentive i : entry.getValue())
            sb.append(entry.getKey()).append("\t").append(i.description()).append("\n");
      return sb.toString();
   }
}
