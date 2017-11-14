package us.kyleedwards.mods.galacticraftclock.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldTimeMessage implements IMessage
{
    private HashMap<Integer, Long> times;

    public WorldTimeMessage()
    {
        this.times = new HashMap<Integer, Long>();
    }

    public WorldTimeMessage(Map<Integer, Long> times)
    {
        this.times = new HashMap<Integer, Long>(times);
    }

    public Map<Integer, Long> getTimes()
    {
        return Collections.unmodifiableMap(this.times);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        while (buf.readableBytes() >= 12)
        {
            int cbodyId = buf.readInt();
            long time = buf.readLong();
            this.times.put(cbodyId, time);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        for (Map.Entry<Integer, Long> entry : this.times.entrySet())
        {
            buf.writeInt(entry.getKey());
            buf.writeLong(entry.getValue());
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
                    for (Map.Entry<Integer, Long> entry : message.getTimes().entrySet())
                    {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                }
            });

            return null;
        }
    }
}
