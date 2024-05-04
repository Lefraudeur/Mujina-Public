package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import static io.github.lefraudeur.Main.mc;
import static io.github.lefraudeur.modules.Module.key_none;

public class PreTickEvent extends Event
{
    public final MinecraftClient instance;
    public PreTickEvent(MinecraftClient instance)
    {
        super();
        this.instance = instance;
    }
    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
        {
            if (module.isEnabled())
                module.onPreTickEvent(this);
        }

        // Keybind stuff
        if (mc.currentScreen != null) return;
        for (Module module : Main.modules)
        {
            if (module.getKeyBind() == key_none) continue;
            boolean isPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), module.getKeyBind());
            if (module.canToggle && isPressed)
            {
                module.toggle();
                module.canToggle = false;
                continue;
            }
            if (!module.canToggle && !isPressed)
                module.canToggle = true;
        }
    }

    public MinecraftClient getInstance()
    {
        return instance;
    }
}
