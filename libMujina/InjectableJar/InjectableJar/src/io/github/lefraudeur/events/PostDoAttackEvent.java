package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.MinecraftClient;

public class PostDoAttackEvent extends PreDoAttackEvent //tail
{
    public PostDoAttackEvent(MinecraftClient instance)
    {
        super(instance);
    }

    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onPostDoAttackEvent(this);
    }
}
