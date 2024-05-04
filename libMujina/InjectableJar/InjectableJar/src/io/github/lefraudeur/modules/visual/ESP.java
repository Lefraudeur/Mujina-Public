package io.github.lefraudeur.modules.visual;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

@Info(category = Category.VISUAL, name = "esp",  description = "ESP", key = Module.key_none)
public final class ESP extends Module {

    StatusEffectInstance glowing = new StatusEffectInstance(StatusEffects.GLOWING, 32767, 0, false, false);

    @Override
    protected void onEnable() {
        //mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, 1, 0));
        //this.toggle();
    }

    @Override
    protected void onDisable() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity.isGlowing())
                ((PlayerEntity) entity).removeStatusEffect(StatusEffects.GLOWING);
        }
    }

    @Override
    public void onPreTickEvent(PreTickEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player.isBlocking() || mc.player.isUsingItem()) return;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity e && !entity.isGlowing() && e != mc.player)
                ((PlayerEntity) entity).addStatusEffect(glowing);
        }
    }
}
