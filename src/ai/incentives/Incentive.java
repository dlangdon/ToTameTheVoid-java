package ai.incentives;

import java.util.HashMap;
import java.util.List;

public class Incentive
{
   public static enum Area
   {
      // Budget allocation incentives
      GROW,                  // Desire to invest in the existing economy
      COLONIZE,              // Desire to colonize new stars to expand economy
      SAVE,                  // Desire to increase or decrease the reserve

      BUILD_SHIPS,           // Desire to invest in new military units, for fighting
      BUILD_TROOPS,          // Desire to build troops, preparing for invasion
      BUILD_BOMBS,           // Desire to build bombers, preparing to annihilate enemy infrastructure
      BUILD_SHIPYARD,        // Desire to increase capability to build more units

      // Unit allocation incentives
      PROTECT,               // Desire to keep ships in the existing territory, to prevent attacks
      EXPAND,                // Desire to send ships into new territory, occupied or not (internal scores will determine these)

      // Trust incentives
      TRUST
   }

   private String description;
   private Area area;
   private float weight;
   private int duration;

   public Incentive(String description, Area area, float weight, int duration)
   {
      this.description = description;
      this.area = area;
      this.weight = weight;
      this.duration = duration;
   }

   public float weight() { return weight; }

   public String description() { return description; }

   public Area area() { return  area; }

   public int duration() { return duration; }
}
