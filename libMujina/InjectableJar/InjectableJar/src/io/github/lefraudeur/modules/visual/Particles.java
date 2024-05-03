package io.github.lefraudeur.modules.visual;

import io.github.lefraudeur.events.AttackEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;

@Info(category = Category.VISUAL, name = "Particles",  description = "particles", key = Module.key_none)
public final class Particles extends Module {

    @Override
    protected void onEnable() {
        //mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, 1, 0));
        //this.toggle();
    }

    @Override
    public void onAttackEvent(final AttackEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player.isBlocking() || mc.player.isUsingItem()) return;
        mc.player.addCritParticles(event.getTarget());
    }
}
