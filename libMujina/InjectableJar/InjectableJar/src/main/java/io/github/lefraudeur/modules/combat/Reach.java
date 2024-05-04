package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.MidUpdateTargetedEntityEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MoveUtils;

@Info(category = Category.COMBAT, name = "Reach",  description = "Make it longer", key = Module.key_none)
public class Reach extends Module {
    private final ValueSetting range = new ValueSetting("range", "The speed you need to go at", 3.0, 3, 6.0, 2);
    private final BooleanSetting onSprint = new BooleanSetting("Sprint", "Only reach when sprinting", true);
    private final BooleanSetting onMove = new BooleanSetting("Move", "Only reach when moving", true);



    private double getReach() {
        if (isNull()) return 0;
        double reach = range.getValue();
        double defaultReach = 3.0;

        if (!mc.player.isSprinting() && onSprint.get()) {
            return defaultReach;
        }

        if (!MoveUtils.hasMovement() && onMove.get()) {
            return defaultReach;
        }

        return reach;
    }

    @Override
    public void onMidUpdateTargetedEntityEvent(MidUpdateTargetedEntityEvent event) {
        event.setNewDoubleValue(Math.pow(getReach(), 2));
    }
}
