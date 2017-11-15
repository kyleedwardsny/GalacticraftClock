package us.kyleedwards.mods.galacticraftclock.tick;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.kyleedwards.mods.galacticraftclock.network.ClockPacketHandler;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeInfo;

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
                HashMap<Integer, DimensionTimeInfo> times = new HashMap<Integer, DimensionTimeInfo>();

                for (WorldServer world : server.worlds)
                {
                    if ((world.provider instanceof WorldProviderSpace && ((WorldProviderSpace) world.provider).getDayLength() > 0)
                            || world.provider instanceof WorldProviderSurface)
                    {
                        DimensionTimeInfo info = new DimensionTimeInfo();
                        info.time = world.provider.getWorldTime();
                        if (world.provider instanceof WorldProviderSpace)
                        {
                            info.dayLength = ((WorldProviderSpace) world.provider).getDayLength();
                        }
                        else
                        {
                            info.dayLength = 24000;
                        }
                        times.put(world.provider.getDimension(), info);
                    }
                }

                ClockPacketHandler.sendWorldTimes(times);
            }
        }
    }
}
