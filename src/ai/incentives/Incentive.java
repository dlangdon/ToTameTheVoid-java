package ai.incentives;

public interface Incentive
{
    public static enum Area { GROWTH, MIGHT, EXPANSION }

    public float weight();
    public String description();
    public Area area();
}
