package io.github.lefraudeur.utils;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static io.github.lefraudeur.Main.mc;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public final class MoveUtils {
    private static final double diagonal = 1 / Math.sqrt(2);
    private static Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

    public static boolean isMoving() {
        return mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0;
    }

    public static boolean isMoving2() {
        return mc.options.forwardKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.backKey.isPressed();
    }

    public static boolean isNull() {
        return (mc.player == null || mc.world == null);
    }

    public static boolean hasMovement() {
        if (isNull()) return false;
        final Vec3d playerMovement = mc.player.getVelocity();
        return playerMovement.getX() != 0 || playerMovement.getY() != 0 || playerMovement.getZ() != 0;
    }

    public static void strafeOnly() {
        setMotion(getSpeed());
    }

    public static Vec3d strafe(double speed, double strength, float yaw, Vec3d vec3d) {
        double prevX = vec3d.x * (1.0 - strength);
        double prevZ = vec3d.z * (1.0 - strength);
        double useSpeed = speed * strength;

        return new Vec3d((-sin(Math.toRadians(yaw)) * useSpeed) + prevX, vec3d.y, (cos(Math.toRadians(yaw)) * useSpeed) + prevZ);
    }

    public static boolean onGround() {
        return mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) (mc.player.getY() - 1E-5D), (int) mc.player.getZ())).getBlock() != Blocks.AIR || mc.player.isOnGround();
    }
    public static double getSpeed()
    {
        return Math.hypot(mc.player.getVelocity().getX(), mc.player.getVelocity().getZ());
    }

    public static void setMotion(final double speed) {
        final Vec3d playerVel = mc.player.getVelocity();

        double moveForward = mc.player.forwardSpeed;
        double moveSideways = mc.player.sidewaysSpeed;

        float yaw = mc.player.getYaw(mc.getTickDelta());

        if (moveForward == 0 && moveSideways == 0)
        {
            mc.player.setVelocity(playerVel.subtract(0, playerVel.y, 0));
        }
        else
        {

            if (moveForward != 0)
            {
                yaw += (float) (moveSideways > 0 ? moveForward > 0 ? -45.0D : 45.0D : moveSideways < 0 ? moveForward > 0 ? 45.0D : -45.0D : 0);
                moveSideways = 0;
                moveForward = moveForward > 0 ? 1 : moveForward < 0 ? -1 : 0;
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0D)), sin = Math.sin(Math.toRadians(yaw + 90.0D));
            final double speedX = moveForward * speed * cos + moveSideways * speed * sin, speedZ = moveForward * speed * sin - moveSideways * speed * cos;

            mc.player.setVelocity(speedX, playerVel.y, speedZ);
        }
    }
    public static double motionY(final double motionY) {
        final Vec3d vec3d = mc.player.getVelocity();
        mc.player.setVelocity(vec3d.x, motionY, vec3d.z);
        return motionY;
    }


    public static double getBaseMoveEventSpeed() {
        double defaultSpeed = 0.2873;

        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            final int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }

        if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            final int amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }

    public static void resetMotionXZ() {
        mc.player.setVelocity(0, mc.player.getVelocity().getY(), 0);
    }


}
