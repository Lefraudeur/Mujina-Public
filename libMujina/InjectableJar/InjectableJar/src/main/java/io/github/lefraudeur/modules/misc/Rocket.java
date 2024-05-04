package io.github.lefraudeur.modules.misc;

import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

@Info(category = Category.MISC, name = "Rocket",  description = "Uses a rocket. Useful for like, elytra stuff", key = Module.key_none)
public final class Rocket extends Module {

    @Override
    protected void onEnable() {
        if (isNull() || mc.interactionManager == null) return;

        int currSlot = mc.player.getInventory().selectedSlot;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.FIREWORK_ROCKET) {
                mc.player.getInventory().selectedSlot = i;
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.getInventory().selectedSlot = currSlot;
                break;
            }
        }
        this.toggle();
    }
}
