package us.kyleedwards.mods.galacticraftclock.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import us.kyleedwards.mods.galacticraftclock.GalacticraftClockMod;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeInfo;

import java.util.Map;

public class ClockPacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(GalacticraftClockMod.MODID);

    public static void registerMessages()
    {
        INSTANCE.registerMessage(WorldTimeMessage.MessageHandler.class, WorldTimeMessage.class, 0, Side.CLIENT);
    }

    public static void sendWorldTimes(Map<Integer, DimensionTimeInfo> times)
    {
        WorldTimeMessage message = new WorldTimeMessage(times);
        INSTANCE.sendToAll(message);
    }
}
