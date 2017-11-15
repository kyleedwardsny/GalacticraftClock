package us.kyleedwards.mods.galacticraftclock;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import us.kyleedwards.mods.galacticraftclock.network.ClockPacketHandler;
import us.kyleedwards.mods.galacticraftclock.screen.GameScreenClock;
import us.kyleedwards.mods.galacticraftclock.tick.TickHandlerServer;

@Mod(modid = GalacticraftClockMod.MODID, version = GalacticraftClockMod.VERSION)
public class GalacticraftClockMod
{
    public static final String MODID = "galacticraftclock";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ClockPacketHandler.registerMessages();
        MinecraftForge.EVENT_BUS.register(TickHandlerServer.class);
        GalacticraftRegistry.registerScreen(new GameScreenClock());
    }
}
