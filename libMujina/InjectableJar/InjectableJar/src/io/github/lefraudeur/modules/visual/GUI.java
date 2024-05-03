package io.github.lefraudeur.modules.visual;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Info(category = Category.VISUAL, name = "GUI",  description = "Opens a GUI to config the cheat", key = GLFW.GLFW_KEY_RIGHT_SHIFT)
public final class GUI extends Module {
    public GUI()
    {
        super();
    }


    public static BooleanSetting background = new BooleanSetting("background", "Should the combat category be visible?", true);
    public static BooleanSetting combatCategory = new BooleanSetting("Combat", "Should the combat category be visible?", true);
    public static BooleanSetting move = new BooleanSetting("Movement", "Should the movement category be visible?", true);
    public static BooleanSetting exploit = new BooleanSetting("Exploit", "Should the exploit category be visible?", true);
    public static BooleanSetting playerCategory = new BooleanSetting("Player", "Should the player category be visible?", true);
    public static BooleanSetting misc = new BooleanSetting("Misc", "Should the Misc category be visible?", true);
    public static BooleanSetting crystal = new BooleanSetting("background", "Should the combat category be visible?", false);

    @Override
    public void onEnable() {
        mc.setScreen(ClickGUI.INSTANCE);
    }

    @Override
    public void onPreTickEvent(PreTickEvent event) {
        if (mc.currentScreen != ClickGUI.INSTANCE || InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_END))
            disable();
    }

    @Override
    public void onDisable()
    {
        mc.setScreen(null);
    }
}
