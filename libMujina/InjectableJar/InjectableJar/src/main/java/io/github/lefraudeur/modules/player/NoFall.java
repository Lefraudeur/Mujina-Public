package io.github.lefraudeur.modules.player;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.PLAYER, name = "NoFall",  description = "You take no fall damage. Spams packets!", key = key_none)
public final class NoFall extends Module {

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
            if (mc.player != null && !mc.player.isOnGround() && mc.player.fallDistance > 2f) {
                send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY() + 0.000001, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                mc.player.onLanding();
        }
    }
}
