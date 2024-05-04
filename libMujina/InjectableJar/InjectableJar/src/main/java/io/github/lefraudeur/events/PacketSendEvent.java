package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;

public class PacketSendEvent extends Event //head
{
    private final ClientCommonNetworkHandler networkHandler;
    private final Packet<?> packet;
    public PacketSendEvent(ClientCommonNetworkHandler networkHandler, Packet<?> packet)
    {
        super();
        this.networkHandler = networkHandler;
        this.packet = packet;
    }

    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onPacketSendEvent(this);
    }

    public ClientCommonNetworkHandler getNetworkHandler()
    {
        return networkHandler;
    }
    public Packet<?> getPacket()
    {
        return packet;
    }
}