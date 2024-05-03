package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.RobotUtil;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Info(category = Category.COMBAT, name = "AutoClicker",  description = "Clicks Auto", key = Module.key_none)
public class AutoClicker extends Module {

    public BooleanSetting lmb = new BooleanSetting("Left mouse", "", true);
    public BooleanSetting rmb = new BooleanSetting("right mouse button", "", true);
    private final ValueSetting cps = new ValueSetting("CPS", "The clicks per second", 14.0, 0.5, 12.0, 2);
    private final ValueSetting minCps = new ValueSetting("CPS", "The clicks per second", 14.0, 0.5, 12.0, 2);
    private final ValueSetting maxCps = new ValueSetting("CPS", "The clicks per second", 14.0, 0.5, 12.0, 2);


    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (lmb.get() && InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1)) {
            try {
                RobotUtil.simulateLeftClick();
            } catch (AWTException ignored) {}
        }
    }
}