package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Info(category = Category.MOVEMENT, name = "HighJump",  description = "Allows you to jump higher.", key = Module.key_none)
public class HighJump extends Module {
    public HighJump() { super(); }

    private final ModeSetting mode = new ModeSetting("Mode", "The highjump mode.", "Vulcan");
    private final ValueSetting multiplier = new ValueSetting("Multiplier", "Motion multiplier.", 1, 1, 10, 1, () -> mode.isMode("Vulcan"));

    private final ValueSetting tickSetting = new ValueSetting("Ticks", "How many ticks to go up", 6, 1, 6, 0, () -> mode.isMode("Vulcan"));

    boolean jumping = false;
    boolean flag = false;
    int ticks = 0;

    @Override
    protected void onEnable() {
        jumping = false;
        flag = false;
        ticks = 0;
    }

    @Override
    public void onPreTickEvent(PreTickEvent event) {
        if (isNull()) return;
        switch (mode.get()) {
            case "Vulcan" -> {
                mc.options.jumpKey.setPressed(false);
                if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.jumpKey.getDefaultKey().getCode()) && !jumping && !flag && mc.player.isOnGround() && mc.currentScreen == null) {
                    if (mc.player.getInventory().getMainHandStack().getItem() instanceof BlockItem && !BlockUtils.cantBePlacedOn(mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock()) && mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() != Blocks.AIR) {
                        Direction side = Direction.DOWN;
                        Vec3d vec = Vec3d.ofCenter(mc.player.getBlockPos());
                        vec = vec.add((float) side.getOffsetX() * 0.5, (float) side.getOffsetY() * 0.5, (float) side.getOffsetZ() * 0.5);
                        BlockHitResult bhr = new BlockHitResult(vec, side.getOpposite(), mc.player.getBlockPos().down(), false);
                        send(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr, 0));
                        jumping = true;
                        ticks = 0;
                        send(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.1, mc.player.getZ(), mc.player.isOnGround()));
                    }
                }

                if (flag) {
                    ticks++;
                    mc.player.setVelocity(mc.player.getVelocity().x, 3.92 * (multiplier.getValue() / 10), mc.player.getVelocity().z);
                    if (ticks >= tickSetting.getInt() + 1) {
                        jumping = false;
                        flag = false;
                    }
                }
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (jumping && !flag && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            flag = true;
        }
    }
}
