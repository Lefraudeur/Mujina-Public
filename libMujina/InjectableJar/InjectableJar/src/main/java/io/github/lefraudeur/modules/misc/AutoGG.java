package io.github.lefraudeur.modules.misc;


import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.player.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.MISC, name = "AutoGG",  description = "Sends message after someone dies.", key = key_none)
public class AutoGG extends Module {
    public AutoGG() { super(); }

    private final ValueSetting distance = new ValueSetting("Distance", "Distance to enemy to count as kill.", 10, 1, 100, 0);

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof EntityStatusS2CPacket packet && packet.getStatus() == 3 && !isNull()) {
            Entity entity = packet.getEntity(mc.world);
            if (!(entity instanceof PlayerEntity) || entity == mc.player) return;

            if (entity.distanceTo(mc.player) > distance.getValue()) return;
            ChatUtils.sendChatMessage("GG", false);
        }
    }
}
