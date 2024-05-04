package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MoveUtils;

@Info(category = Category.MOVEMENT, name = "VulcannotBhop",  description = "BIG BHOP!!!!!!", key = Module.key_none)
public final class VulcanBHop extends Module {
    public VulcanBHop() { super(); }

    @Override
    protected void onDisable() {
        mc.options.jumpKey.setPressed(false);
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {

        // setting the bps on some random number above 11 and under 12 bc like rounded numbers get flagged "vulcan is an advaneced anticheat" JOKES
        // Vec3d vel = MoveUtils.getHorizontalVelocity(11.6345);

        // getting velocity X and Z
        //double velX = vel.getX();
        //double velZ = vel.getZ();

        // returning if the player isn't on ground else it'll flag for strafe or something
        if (!mc.player.isOnGround()) return;

        // setting the X and Z movement

        //((IVec3d) event.movement).set(velX, event.movement.y, velZ);
        MoveUtils.setMotion(0.11521);

        if (MoveUtils.hasMovement()) mc.player.jump();
    }
}
