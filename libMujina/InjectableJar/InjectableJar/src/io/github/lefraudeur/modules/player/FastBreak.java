package io.github.lefraudeur.modules.player;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.PLAYER, name = "FastBreak",  description = "Breaks Fast!", key = key_none)
public final class FastBreak extends Module {

    StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE, 32767, 0, false, false);
    @Override
    protected void onEnable() {
        if (isNull()) return;
        mc.player.addStatusEffect(haste);
    }

    @Override
    protected void onDisable() {
        if (isNull()) return;
        mc.player.removeStatusEffect(StatusEffects.HASTE);
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;
        if (!mc.player.getStatusEffects().contains(StatusEffects.HASTE) || mc.player.getStatusEffects().isEmpty()) mc.player.addStatusEffect(haste);
    }
}
