package io.github.lefraudeur.utils;

import io.github.lefraudeur.modules.movement.Flight;
import net.minecraft.entity.player.PlayerEntity;

import static io.github.lefraudeur.Main.mc;

public class AuraUtils {

    public static boolean canCrit() {
        return (!mc.player.isOnGround() && MoveUtils.hasMovement() && mc.player.fallDistance >= 0.065 && mc.player.getAttackCooldownProgress(0.5f) >= 0.86)
                || (hasFlyUtils() && mc.player.getAttackCooldownProgress(0.5f) >= 0.86);
    }

    public static boolean hasFlyUtils() {
        return mc.player.getAbilities().flying;
        // return mc.player.getAbilities().flying || Main.getModuleByClass(Glide.class).isEnabled() || Main.getModuleByClass(Flight.class).isEnabled();
    }

    public static boolean shouldBreakShield(final PlayerEntity player) {
        return player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player)); // && disableShields.get();
    }
}
