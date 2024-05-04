package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MoveUtils;

@Info(category = Category.MOVEMENT, name = "Sprint",  description = "Sprint!!!", key = Module.key_none)
public final class Sprint extends Module {
    public Sprint() { super(); }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (MoveUtils.hasMovement()) mc.options.sprintKey.setPressed(true);
    }
}
