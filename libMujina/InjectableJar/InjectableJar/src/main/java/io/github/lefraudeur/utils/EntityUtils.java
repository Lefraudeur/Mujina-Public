package io.github.lefraudeur.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;

public final class EntityUtils {
    public static boolean isAnimal(Entity e) {
        return e instanceof PassiveEntity
                || e instanceof AmbientEntity
                || e instanceof WaterCreatureEntity
                || e instanceof IronGolemEntity
                || e instanceof SnowGolemEntity;
    }
}