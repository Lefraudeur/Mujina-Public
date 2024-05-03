package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.AttackEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

@Info(category = Category.COMBAT, name = "wTap",  description = "wTap funny!", key = Module.key_none)
public final class wTap extends Module {

    @Override
    protected void onEnable() {
        //mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, 1, 0));
        //this.toggle();
    }

    @Override
    public void onAttackEvent(final AttackEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player.isBlocking() || mc.player.isUsingItem()) return;
        if (event.getTarget() != null && mc.player.isOnGround()) send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
    }
}
