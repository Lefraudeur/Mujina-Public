package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreDoAttackEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.misc.Teams;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

@Info(category = Category.COMBAT, name = "NoMiss",  description = "Prevents misses", key = Module.key_none)
public final class NoMiss extends Module {

    private final BooleanSetting playerOnly = new BooleanSetting("NoTeam", "Cancels hits on teammates", false);
    private final BooleanSetting hitCooldown = new BooleanSetting("hitcooldown", "Cancels hits on teammates", false);

    private final ValueSetting cooldown = new ValueSetting("cooldown", "AAA", 0.912, 0.1, 1, 2);
    @Override
    protected void onEnable() {
        //mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, 1, 0));
        //this.toggle();
    }

    @Override
    public void onPreDoAttackEvent(final PreDoAttackEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player.isBlocking() || mc.player.isUsingItem()) return;

        if (mc.crosshairTarget instanceof EntityHitResult entity && playerOnly.get()) {
            if (Teams.isTeam(entity.getEntity())) event.cancel(false);
        }

        if (hitCooldown.get() && mc.player.getAttackCooldownProgress(0.5f) < cooldown.getValue()) {
            event.cancel(false);
        }

        if (mc.crosshairTarget.getType().equals(HitResult.Type.MISS))
            event.cancel(false);
    }
}