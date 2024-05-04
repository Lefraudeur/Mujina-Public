package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.MinecraftClient;

public class PreDoAttackEvent extends Event //MinecraftClient.doAttack, leftClick basically
{
    private final MinecraftClient instance;
    private boolean returnValue; // !!!This is the returnValue set if you cancel the event !!!
    public PreDoAttackEvent(MinecraftClient instance)
    {
        this.instance = instance;
        returnValue = false;
    }
    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onPreDoAttackEvent(this);
    }

    public MinecraftClient getInstance()
    {
        return instance;
    }

    public boolean getReturnValue()
    {
        return returnValue;
    }

    public void cancel(boolean returnValue)
    {
        this.returnValue = returnValue;
        setCancelled(true);
    }
}
