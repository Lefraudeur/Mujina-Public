package io.github.lefraudeur.modules.player;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.BlockUtils;
import net.minecraft.util.Hand;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.PLAYER, name = "Scaaffold",  description = "Scaffold...", key = key_none)
public final class Scaffold extends Module {

    @Override
    protected void onEnable() {
        if (isNull()) return;
    }

    @Override
    protected void onDisable() {
        if (isNull()) return;
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;

        BlockUtils.place(mc.player.getBlockPos().down(), Hand.MAIN_HAND, BlockUtils.RotationPacket.Packet2, BlockUtils.Swing.Client);
    }
}
