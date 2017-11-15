package us.kyleedwards.mods.galacticraftclock.tick;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeRegistry;

public class TickHandlerClient
{
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            DimensionTimeRegistry.doTick();
        }
    }
}
