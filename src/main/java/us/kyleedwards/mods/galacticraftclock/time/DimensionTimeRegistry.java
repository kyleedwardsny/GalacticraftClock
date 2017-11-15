package us.kyleedwards.mods.galacticraftclock.time;

import java.util.HashMap;
import java.util.Map;

public class DimensionTimeRegistry
{
    public static HashMap<Integer, DimensionTimeInfo> dimensionTimes = new HashMap<Integer, DimensionTimeInfo>();

    public static void doTick()
    {
        for (Map.Entry<Integer, DimensionTimeInfo> entry : dimensionTimes.entrySet())
        {
            entry.getValue().time++;
        }
    }

    public static void setDimensionTimes(Map<Integer, DimensionTimeInfo> times)
    {
        dimensionTimes.clear();
        dimensionTimes.putAll(times);
    }
}
