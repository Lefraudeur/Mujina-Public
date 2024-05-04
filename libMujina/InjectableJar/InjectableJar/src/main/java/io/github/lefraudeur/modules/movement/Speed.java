package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.BlockCollisionEvent;
import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.exploit.Disabler;
import io.github.lefraudeur.utils.MoveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import static io.github.lefraudeur.utils.MoveUtils.getBaseMoveEventSpeed;

@Info(category = Category.MOVEMENT, description = "Allows you to move faster.", key = GLFW.GLFW_KEY_V, name = "Bhop")
public class Speed extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "The Bhop mode.", "Vanilla", "Matrix1", "Grim", "Vulcan", "VulcanGround");
    private final ValueSetting speedSetting = new ValueSetting("Speed", "The speed you need to go at", 6.0, 0.5, 12.0, 2);
    public ValueSetting grimBoost = new ValueSetting("Grim Boost", "", 1.15, 1, 2, 2, () -> mode.isMode("Grim"));
    public ValueSetting grimDist = new ValueSetting("Distance", "", 1.5, 0, 3, 1, () -> mode.isMode("Grim"));
    public BooleanSetting grimStrafe = new BooleanSetting("Grim Strafe", "", true, () -> mode.isMode("Grim"));
    public ValueSetting grimStrafeAmount = new ValueSetting("Grim Strafe Amount", "", 0.2, 1, 1, 2, () -> mode.isMode("Grim"));
    



    private double speed, level, lastSpeed;

    private boolean touchedGround;

    private int ticks, groundTicks, airTicks, stage;

    @Override
    protected void onEnable() {
        touchedGround = mc.player.isOnGround();
        ticks = 0;
        level = 0;
        stage = 0;
        speed = getBaseMoveEventSpeed();
    }

    @Override
    protected void onDisable() {
        MoveUtils.resetMotionXZ();
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;

        switch (mode.get()) {
            case "Matrix1":
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                    this.speed = 0.34f;
                    ++this.groundTicks;
                    this.airTicks = 0;
                } else {
                    this.groundTicks = 0;
                    ++this.airTicks;
                    this.speed *= 0.98;
                    MoveUtils.setMotion(speed);
                }
                break;

            case "Vanilla":
                MoveUtils.setMotion(speedSetting.getValue());
                break;

            case "Vulcan":
                if (mc.player.isOnGround()) {
                    mc.player.jump();
                    MoveUtils.setMotion(speed);
                }
                break;

            case "VulcanGround":
                if (!Disabler.enabled || Disabler.fail) return;
                if (!mc.options.jumpKey.isPressed() && MoveUtils.isMoving()) {
                    if (mc.player.isOnGround()) {
                        ticks = 0;
                        mc.player.setVelocity(mc.player.getVelocity().x, 0.01, mc.player.getVelocity().z);
                        if (mc.player.isSprinting()) {
                            float rad = (float) Math.toRadians(mc.player.getYaw());
                            mc.player.setVelocity(mc.player.getVelocity().add(-MathHelper.sin(rad) * 0.2f, 0.0, MathHelper.cos(rad) * 0.2f));
                        }
                    } else {
                        ticks++;
                        if (ticks == 1)
                            mc.player.setVelocity(mc.player.getVelocity().x, 0.01, mc.player.getVelocity().z);
                    }
                    mc.player.setVelocity(MoveUtils.strafe(MoveUtils.getSpeed(), 1, mc.player.getYaw(), mc.player.getVelocity()));


                }
                break;


            case "Grim":
                for (Entity entity : mc.world.getEntities()){
                    if (Math.sqrt(mc.player.squaredDistanceTo(entity)) <= grimDist.getValue() && isSpeedable(entity)) {
                        if (!mc.player.isOnGround()) {
                            if (grimStrafe.get())
                                mc.player.setVelocity(MoveUtils.strafe(MoveUtils.getSpeed(), grimStrafeAmount.getValue(), mc.player.getYaw(), mc.player.getVelocity()));

                            double boost = grimBoost.getValue();

                            double velocityX = mc.player.getVelocity().x * boost;
                            double velocityZ = mc.player.getVelocity().z * boost;
                            mc.player.setVelocity(velocityX, mc.player.getVelocity().y, velocityZ);
                        }


                        // Doesnt stack velocity from different players
                        break;
                    }
                }

                break;
        }
    }

    @Override
    public void onBlockCollisionEvent(BlockCollisionEvent event) {
        /*
        switch (mode.get()) {
            case "VulcanGround":
                // Semi fast
                //if (!mc.options.jumpKey.isPressed() && Objects.equals(event.getPos(), mc.player.getBlockPos().up().up())) event.cancel(Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0));
                // Faster speed
                //if (!mc.options.jumpKey.isPressed() && Objects.equals(event.getPos(), mc.player.getBlockPos().up().up())) event.cancel(VoxelShapes.fullCube());
                // Fastest

                if (!mc.options.jumpKey.isPressed() && Objects.equals(event.getPos(), mc.player.getBlockPos().up())) event.cancel(Block.createCuboidShape(0.0, 15, 0.0, 16.0, 16.0, 16.0));

                break;
        }

         */
    }

    private boolean isSpeedable(Entity entity) {
        if (entity == mc.player) return false;
        if (!(entity instanceof LivingEntity)) return false;
        if (entity instanceof ArmorStandEntity) return false;
        return true;
    }

}
