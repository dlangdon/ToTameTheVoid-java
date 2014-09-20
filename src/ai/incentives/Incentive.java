package ai.incentives;

public interface Incentive
{
    public static enum Area
    {
       // Budget allocation incentives
       GROW,                  // Desire to invest in the existing economy
       COLONIZE,              // Desire to colonize new stars to expand economy
       BUILD_SHIPS,           // Desire to invest in new military units, for fighting
       BUILD_TROOPS,          // Desire to build troops, preparing for invasion
       BUILD_BOMBS,           // Desire to build bombers, preparing to annihilate enemy infrastructure
       BUILD_SHIPYARD,        // Desire to increase capability to build more units

       // Unit allocation incentives
       DEFENSE,               // Desire to keep ships in the existing territory, to prevent attacks
       EXPANSION              // Desire to send ships into new territory, occupied or not (internal scores will determine these)
    }

    public float weight();
    public String description();
    public Area area();
}
