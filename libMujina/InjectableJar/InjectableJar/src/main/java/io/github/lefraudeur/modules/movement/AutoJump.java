package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MoveUtils;
import org.lwjgl.glfw.GLFW;

@Info(category = Category.MOVEMENT, name = "AutoJump",  description = "Jumps automatically!!!", key = GLFW.GLFW_KEY_Z)
public final class AutoJump extends Module {
    public AutoJump() { super(); }

    @Override
    protected void onDisable() {
        mc.options.jumpKey.setPressed(false);
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (MoveUtils.hasMovement()) mc.options.jumpKey.setPressed(true);
    }
}
