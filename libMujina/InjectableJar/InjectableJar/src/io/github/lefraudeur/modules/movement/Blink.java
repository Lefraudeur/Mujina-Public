package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PacketSendEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ConcurrentLinkedQueue;

@Info(category = Category.MOVEMENT, name = "Blink",  description = "Cancels packets or whatever", key = GLFW.GLFW_KEY_B)
public final class Blink extends Module
{
    private final ConcurrentLinkedQueue<Packet<?>> saved_packets = new ConcurrentLinkedQueue<>();
    public Blink()
    {
        super();
    }

    @Override
    protected void onDisable()
    {
        if(saved_packets.isEmpty()) return;
        saved_packets.forEach((Packet<?> p)-> MinecraftClient.getInstance().getNetworkHandler().sendPacket(p));
        saved_packets.clear();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event)
    {
        if (isNull()) return;
        saved_packets.add(event.getPacket());
        event.setCancelled(true);
    }
}
