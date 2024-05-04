package io.github.lefraudeur.modules.misc;

import io.github.lefraudeur.events.PacketSendEvent;
import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Info(category = Category.MISC, name = "CivBreak",  description = "Funy break :D", key = Module.key_none)
public class CivBreak extends Module {
    public CivBreak() { super(); }
    private BlockPos pos;
    private Direction direction;

    @Override
    protected void onEnable() {
        pos = null;
        direction = null;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof PlayerActionC2SPacket packet && packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            pos = packet.getPos();
            direction = packet.getDirection();
        }
    }

    @Override
    public void onPreTickEvent(PreTickEvent event) {
        if (direction != null && pos != null) {
            if (Math.sqrt(mc.player.squaredDistanceTo(Vec3d.ofCenter(pos))) > 6) {
                this.toggle();
                return;
            }
            if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) return;
            send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, direction));
        }
    }
}
