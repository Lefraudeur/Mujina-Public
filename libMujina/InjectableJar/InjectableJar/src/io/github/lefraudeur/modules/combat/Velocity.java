package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Info(category = Category.COMBAT, name = "Velocity",  description = "Make it longer", key = Module.key_none)
public class Velocity extends Module {


    private final ModeSetting mode = new ModeSetting("Mode", "The Bhop mode.", "Vanilla", "Grim");
    private final ValueSetting delay = new ValueSetting("delay", "The speed you need to go at", 6.0, 0.5, 12.0, 2);
    public BooleanSetting funny = new BooleanSetting("funny", "", true);
    public BooleanSetting always = new BooleanSetting("funny", "", true);

    int timeout;
    boolean velo = false;

    @Override
    protected void onDisable() {
        velo = false;
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {

        if ((always.get() || velo) && mode.isMode("Grim"))
            doFunny();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (mode.isMode("Grim")) {
            if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
                timeout = delay.getInt();
            } else if (((e.getPacket() instanceof EntityVelocityUpdateS2CPacket packet && timeout <= 0 && packet.getId() == mc.player.getId()) || e.getPacket() instanceof ExplosionS2CPacket)) {
                e.setCancelled(true);
                velo = true;
            } else timeout--;
        }
    }

    private void doFunny() {
        velo = false;
        if (funny.get()) // 1.17 funny
            send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, (BlockPos) mc.player.getBlockPos().up(), Direction.DOWN));
    }
}
