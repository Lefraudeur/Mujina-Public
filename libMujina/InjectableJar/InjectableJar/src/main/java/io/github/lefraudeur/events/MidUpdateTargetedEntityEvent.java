package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.render.GameRenderer;

public class MidUpdateTargetedEntityEvent extends Event
{
    private GameRenderer gameRenderer;
    private float tickDelta;
    private double newDoubleValue = 9.0;

    public MidUpdateTargetedEntityEvent(GameRenderer gameRenderer, float tickDelta)
    {
        this.gameRenderer = gameRenderer;
        this.tickDelta = tickDelta;
    }

    @Override
    public void dispatch()
    {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onMidUpdateTargetedEntityEvent(this);
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public void setNewDoubleValue(double newDoubleValue) {
        this.newDoubleValue = newDoubleValue;
    }

    public double getNewDoubleValue()
    {
        return this.newDoubleValue;
    }
}
