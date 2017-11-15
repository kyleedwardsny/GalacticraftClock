package us.kyleedwards.mods.galacticraftclock.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeInfo;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldTimeMessage implements IMessage
{
    private HashMap<Integer, DimensionTimeInfo> times;

    public WorldTimeMessage()
    {
        this.times = new HashMap<Integer, DimensionTimeInfo>();
    }

    public WorldTimeMessage(Map<Integer, DimensionTimeInfo> times)
    {
        this.times = new HashMap<Integer, DimensionTimeInfo>(times);
    }

    public Map<Integer, DimensionTimeInfo> getTimes()
    {
        return Collections.unmodifiableMap(this.times);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        while (buf.readableBytes() > 0)
        {
            int dimensionID = buf.readInt();
            DimensionTimeInfo info = new DimensionTimeInfo();
            info.time = buf.readLong();
            info.dayLength = buf.readLong();
            this.times.put(dimensionID, info);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        for (Map.Entry<Integer, DimensionTimeInfo> entry : this.times.entrySet())
        {
            buf.writeInt(entry.getKey());
            buf.writeLong(entry.getValue().time);
            buf.writeLong(entry.getValue().dayLength);
        }
    }

    public static class MessageHandler implements IMessageHandler<WorldTimeMessage, IMessage>
    {
        @Override
        public IMessage onMessage(final WorldTimeMessage message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable()
            {
                @Override
                public void run()
                {
                    DimensionTimeRegistry.setDimensionTimes(message.getTimes());
                }
            });

            return null;
        }
    }
}
