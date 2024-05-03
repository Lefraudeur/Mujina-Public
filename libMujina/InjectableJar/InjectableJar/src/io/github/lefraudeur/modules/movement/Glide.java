package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;

@Info(category = Category.MOVEMENT, name = "Glide",  description = "Allows you to glide around. Made for vulcan.", key = Module.key_none)
public final class Glide extends Module {
    public Glide() { super(); }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;
        if (mc.player.getVelocity().y <= -0.1) {
            if (mc.player.age % 2 == 0) {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.1, mc.player.getVelocity().z);
            } else {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.16, mc.player.getVelocity().z);
            }
        }
    }
}
