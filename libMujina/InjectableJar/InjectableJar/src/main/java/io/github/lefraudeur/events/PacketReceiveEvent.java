package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;

public class PacketReceiveEvent extends Event //head
{
    private final ClientConnection clientConnection;
    private final ChannelHandlerContext channelHandlerContext;
    private final Packet<?> packet;
    public PacketReceiveEvent(ClientConnection clientConnection, ChannelHandlerContext channelHandlerContext, Packet<?> packet)
    {
        this.clientConnection = clientConnection;
        this.channelHandlerContext = channelHandlerContext;
        this.packet = packet;
    }

    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onPacketReceiveEvent(this);
    }

    public ClientConnection getClientConnection()
    {
        return clientConnection;
    }
    public ChannelHandlerContext getChannelHandlerContext()
    {
        return channelHandlerContext;
    }
    public Packet<?> getPacket()
    {
        return packet;
    }
}
