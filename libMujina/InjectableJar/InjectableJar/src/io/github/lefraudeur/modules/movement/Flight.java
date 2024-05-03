package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.BlockCollisionEvent;
import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MoveUtils;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Info(category = Category.MOVEMENT, name = "Flight",  description = "Allows you to fly around.", key = GLFW.GLFW_KEY_G)
public final class Flight extends Module {
    public Flight() { super(); }

    private final ModeSetting mode = new ModeSetting("Mode", "The mode to use", "Vanilla", "Vulcan");

    private final ValueSetting speedSetting = new ValueSetting("Speed", "The speed you need to go at", 6.0, 0.5, 12.0, 2, () -> mode.isMode("Vanilla"));


    PlayerPositionLookS2CPacket packet;
    int flags;
    boolean wait;

    @Override
    protected void onEnable() {
        switch (mode.get()) {
            case "Vulcan" -> {
                packet = null;
                wait = false;
                flags = 0;
                send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.1, mc.player.getZ(), mc.player.isOnGround()));
            }
        }
    }

    @Override
    protected void onDisable() {
        switch (mode.get()) {
            case "Vulcan" -> {
                if (packet != null) {
                    packet.apply(mc.getNetworkHandler());
                    packet = null;
                    wait = true;
                }
            }
        }
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;
        switch (mode.get()) {
            case "Vanilla" -> {
                mc.player.setVelocity(0, 0, 0);
                if (MoveUtils.isMoving2()) MoveUtils.setMotion(speedSetting.getValue());

                if (mc.options.jumpKey.isPressed()) MoveUtils.motionY(speedSetting.getValue());

                if (mc.options.sneakKey.isPressed()) MoveUtils.motionY(-speedSetting.getValue());

                // MoveUtils.setMotion(speedSetting.getValue());
            }

            case "Vulcan" -> {
                if (mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() != Blocks.AIR && wait) {
                    mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
                }
                if (mc.options.useKey.isPressed() && packet != null) {
                    packet.apply(mc.getNetworkHandler());
                    packet = null;
                    wait = true;
                }
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        switch (mode.get()) {
            case "Vulcan" -> {
                if (event.getPacket() instanceof PlayerPositionLookS2CPacket p) {
                    flags++;
                    if (flags == 1) {
                        packet = p;
                        event.cancel();
                    } else {
                        this.toggle();
                    }
                }
            }
        }
    }

    @Override
    public void onBlockCollisionEvent(BlockCollisionEvent event) {
        switch (mode.get()) {
            case "Vulcan" -> {
                if (Objects.equals(event.getPos(), new BlockPos(mc.player.getBlockX(), mc.player.getBlockY() - 1, mc.player.getBlockZ())) && !mc.player.isSneaking()) event.cancel(VoxelShapes.fullCube());
                else if (!wait && !Objects.equals(event.getPos(), new BlockPos(mc.player.getBlockX(), mc.player.getBlockY() - 1, mc.player.getBlockZ()))) event.cancel(VoxelShapes.empty());
            }
        }
    }
}