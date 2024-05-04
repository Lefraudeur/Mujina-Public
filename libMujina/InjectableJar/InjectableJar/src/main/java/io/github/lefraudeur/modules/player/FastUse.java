package io.github.lefraudeur.modules.player;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.PLAYER, name = "Fast Use",  description = "Use stuff fast...", key = key_none)
public final class FastUse extends Module {

    @Override
    protected void onEnable() {
        if (isNull()) return;
    }

    @Override
    protected void onDisable() {
        if (isNull()) return;
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;
        if (!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;
        BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
        if (mc.options.useKey.isPressed())
            if (mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult) == ActionResult.SUCCESS)
                mc.player.swingHand(Hand.MAIN_HAND);
    }
}
