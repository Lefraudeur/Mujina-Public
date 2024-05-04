package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreRender2DEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.misc.Teams;
import io.github.lefraudeur.utils.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Info(category = Category.COMBAT, name = "AimAssist",  description = "Aims for you...", key = Module.key_none)
public final class AimAssist extends Module {

    private final BooleanSetting playerOnly = new BooleanSetting("Players Only", "Aim only players", true);
    private final BooleanSetting invisibles = new BooleanSetting("Invisibles", "Aim only players", true);
    private final BooleanSetting requireLeftClick = new BooleanSetting("Click", "Aim only when left click is pressed", true);
    private final ValueSetting maxDistance = new ValueSetting("Max Distance", "Target max distance", 6.0, 0.5, 12.0, 2);
    private final ValueSetting fov = new ValueSetting("FOV", "The FOV", 360, 1, 360, 0);
    private final ValueSetting maxAngle = new ValueSetting("Max Angle", "The maximum angle the aimassist can rotate", 70.0, 10.0, 180.0, 2);
    private final BooleanSetting slot = new BooleanSetting("Slot", "Aim only when the slot is a specific slot", true);
    private final ValueSetting slotNumber = new ValueSetting("SlotNumber", "The slot it should be", 1, 1, 9, 0);
    private Entity lockedTarget = null;

    @Override
    public void onPreRender2DEvent(final PreRender2DEvent event) {
        if (mc.player == null || mc.world == null || (!mc.options.attackKey.isPressed() && requireLeftClick.get())) {
            lockedTarget = null;
            return;
        }

        Vec3d cameraPos = mc.player.getCameraPosVec(event.getTickDelta());
        Vec2f cameraRot = clamp180(mc.player.getRotationClient());
        Vec3d maxPos = getMaxPos(cameraPos, cameraRot, maxDistance.getValue());

        Entity selectedTarget = null;
        Vec2f rotationDelta = null;

        for (Entity entity : mc.world.getEntities()) {

            // DONE: integrate with Teams

            if (entity == null || entity == mc.player || !entity.isLiving() || (entity instanceof PlayerEntity && Teams.isTeam(entity))) continue;
            if (!MathUtils.isInFOV(entity, fov.getInt()) || (invisibles.get() && entity.isInvisibleTo(mc.player)) || entity.isInvulnerable()) continue;
            if (slot.get() && mc.player.getInventory().selectedSlot != slotNumber.getInt() - 1) continue;
            if (playerOnly.get() && !(entity.isPlayer())) continue;
            Vec3d targetPos = entity.getCameraPosVec(event.getTickDelta());
            Vec2f targetRotationDelta = getRotationDelta(cameraRot, cameraPos, targetPos);
            if (cameraPos.distanceTo(targetPos) > maxDistance.getValue()) continue;
            if (Math.abs(targetRotationDelta.y) > maxAngle.getFloat()) continue;

            if (entity == lockedTarget) {
                selectedTarget = lockedTarget;
                rotationDelta = targetRotationDelta;
                break;
            }

            if (rotationDelta == null || Math.abs(targetRotationDelta.y) < Math.abs(rotationDelta.y))
            {
                selectedTarget = entity;
                rotationDelta = targetRotationDelta;
            }
        }


        if (rotationDelta == null)
        {
            lockedTarget = null;
            return;
        }
        lockedTarget = selectedTarget;
        if (selectedTarget.getBoundingBox().raycast(cameraPos, maxPos).isPresent()) return;
        mc.player.setPitch(mc.player.getPitch() + 0.1f * rotationDelta.x);
        mc.player.setYaw(mc.player.getYaw() + 0.1f * rotationDelta.y);
    }

    @Override
    protected void onDisable()
    {
        lockedTarget = null;
    }

    private Vec2f getRotationDelta(Vec2f cameraRot, Vec3d cameraPos, Vec3d targetPos) {
        Vec3d delta = targetPos.subtract(cameraPos);

        double hypxz = delta.length();
        if (hypxz == 0.0) return new Vec2f(0.0f, 0.0f);

        double pitchRad = Math.atan(-delta.y / hypxz);

        double yawDeg = 0.0;
        double pitchDeg = Math.toDegrees(pitchRad);

        if (delta.x != 0.0)
        {
            double yawRad = Math.atan2(delta.z, delta.x) - Math.PI / 2;
            yawDeg = Math.toDegrees(yawRad);
        }

        float deltaPitch = (float)pitchDeg - cameraRot.x;
        float deltaYaw = (float)yawDeg - cameraRot.y;

        return clamp180(new Vec2f(deltaPitch, deltaYaw));
    }

    private Vec3d getMaxPos(Vec3d cameraPos, Vec2f cameraRot, double d)
    {
        final double hypxz = d * Math.cos(-Math.toRadians(cameraRot.x));

        return cameraPos.add(new Vec3d(Math.sin(-Math.toRadians(cameraRot.y)) * hypxz, d * Math.sin(-Math.toRadians(cameraRot.x)), Math.cos(-Math.toRadians(cameraRot.y)) * hypxz));
    }

    private Vec2f clamp180(Vec2f angles)
    {
        return new Vec2f(clamp180(angles.x), clamp180(angles.y));
    }

    private float clamp180(float angle)
    {
        while (angle <= -180.0f)
            angle += 360.0f;
        while (angle > 180.0f)
            angle -= 360.0f;

        return angle;
    }

    private float random(float min, float max)
    {
        return (float)Math.random() * (max - min) + min;
    }
}
