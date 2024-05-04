package io.github.lefraudeur.modules.movement;

import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;

@Info(category = Category.MOVEMENT, name = "Clip",  description = "Allows you to clip...", key = Module.key_none)
public final class Clip extends Module {
    public Clip() { super(); }

    private final ModeSetting mode = new ModeSetting("Mode", "The clip mode.", "Vanilla");
    private final ValueSetting blocks = new ValueSetting("Blocks", "the blocks to clip", 10, 1, 200, 0, () -> mode.isMode("Vanilla"));

    @Override
    protected void onEnable() {
        switch (mode.get()) {
            case "Vanilla" -> {
                mc.player.updatePosition(mc.player.getX(), mc.player.getY() + blocks.getInt(), mc.player.getZ());
                this.toggle();
            }
        }
    }

}
