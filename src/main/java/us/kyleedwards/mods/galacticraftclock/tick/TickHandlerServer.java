package us.kyleedwards.mods.galacticraftclock.tick;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.kyleedwards.mods.galacticraftclock.network.ClockPacketHandler;

import java.util.HashMap;

public class TickHandlerServer
{
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null)
        {
            return;
        }

        if (event.phase == TickEvent.Phase.START)
        {
            if (server.getTickCounter() % 20 == 0)
            {
                HashMap<Integer, Long> times = new HashMap<Integer, Long>();
                times.put(0, server.getTickCounter() + 1000L);
                times.put(1, server.getTickCounter() + 2000L);
                System.out.println("About to send times");
                ClockPacketHandler.sendWorldTimes(times);
            }
        }
    }
}
