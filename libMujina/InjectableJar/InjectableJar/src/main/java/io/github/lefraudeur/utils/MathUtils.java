package io.github.lefraudeur.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.lefraudeur.Main.mc;
import static io.github.lefraudeur.utils.player.PlayerUtils.squaredDistanceTo;

public final class MathUtils {

    public static boolean chance(int min, int max, int desired) {
        return MathHelper.nextInt(Random.create(), min, max + 1) > desired;
    }

    public static boolean chance(int min, int max, double desired) {
        return MathHelper.nextDouble(Random.create(), min, max + 1) > desired;
    }

    public static double getRandomDouble(double from, double to) {
        if (from >= to) return from;
        return ThreadLocalRandom.current().nextDouble(from, to);
    }

    public static double round(final double value, final int decimalAmount)
    {
        if(decimalAmount < 0) throw new IllegalArgumentException();
        final BigDecimal bd = BigDecimal.valueOf(value).setScale(decimalAmount, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundTo(final double value, final double roundResult)
    {
        return Math.round(value / roundResult) * roundResult;
    }

    public static double round(final double value)
    {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(3, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        float f = (float) (x1 - x2);
        float g = (float) (y1 - y2);
        float h = (float) (z1 - z2);
        return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
    }


    public static boolean isWithin(Entity entity, double r) {
        return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= r * r;
    }

    public static boolean isWithin(Vec3d vec3d, double r) {
        return squaredDistanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ()) <= r * r;
    }

    public static boolean isWithin(BlockPos blockPos, double r) {
        return squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= r * r;
    }

    public static boolean isWithin(double x, double y, double z, double r) {
        return squaredDistanceTo(x, y, z) <= r * r;
    }

    public static double distanceToCamera(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToCamera(x, y, z));
    }

    public static double distanceToCamera(final Entity entity) {
        return distanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static double squaredDistanceToCamera(double x, double y, double z) {
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        return squaredDistance(cameraPos.x, cameraPos.y, cameraPos.z, x, y, z);
    }

    public static double squaredDistanceToCamera(final Entity entity) {
        return squaredDistanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }


    public static boolean isInFOV(final Entity entity, double angle) {
        angle *= 0.5;

        if(entity != null) {
            final double angleDiff = getAngleDifference(mc.player.getYaw(), getRotations(entity)[0]);

            return angleDiff > 0 && angleDiff < angle || -angle < angleDiff && angleDiff < 0 && entity != null;
        }
        return false;
    }

    public static int getRandomNumber(int max, int min) {
        return  -min + (int) (Math.random() * ((max - (-min)) + 1));
    }

    public static float[] getRotations(final Entity ent) {
        final double x = ent.getPos().getX();
        final double y = ent.getPos().getY() + ent.getEyeHeight(ent.getPose());
        final double z = ent.getPos().getZ();
        return getRotationFromPosition(x, y, z);
    }

    public static float[] getRotationFromPosition(final double x, final double y, final double z) {
        final double xDiff = x - mc.player.getPos().getX();
        final double yDiff = y - (mc.player.getPos().getY() + mc.player.getEyeHeight(mc.player.getPose()));
        final double zDiff = z - mc.player.getPos().getZ();

        final double dist = MathHelper.hypot(xDiff, zDiff);
        final float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0f;
        final float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);

        return new float[]
                { yaw, pitch };
    }

    public static float getAngleDifference(final float dir, final float yaw) {
        final float f = Math.abs(yaw - dir) % 360;
        float dist = f;

        if(f > 180.0F) dist = 360.0F - f;

        return dist;
    }
}