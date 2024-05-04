package io.github.lefraudeur.utils.player;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;

import static io.github.lefraudeur.Main.mc;

public class PlayerUtils {

    public static boolean isWithin(double x, double y, double z, double r) {
        return squaredDistanceTo(x, y, z) <= r * r;
    }

    public static double squaredDistanceTo(double x, double y, double z) {
        return squaredDistance(mc.player.getX(), mc.player.getY(), mc.player.getZ(), x, y, z);
    }

    public static double squaredDistanceTo(Entity entity) {
        return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public static void addMessage(final String text) {
        mc.inGameHud.getChatHud().addMessage(Text.literal(text));
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        float f = (float) (x1 - x2);
        float g = (float) (y1 - y2);
        float h = (float) (z1 - z2);
        return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
    }

    public static int getPing(final Entity entity) {
        if (entity == null) {
            return 0;
        }

        final PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());

        return playerEntry == null ? 0 : playerEntry.getLatency();
    }

    public static boolean hasEffect(ItemStack stack, StatusEffect effect) {
        for(StatusEffectInstance effectInstance : PotionUtil
                .getPotionEffects(stack))
        {
            if(effectInstance.getEffectType() != effect)
                continue;
            return true;
        }

        return false;
    }

}
