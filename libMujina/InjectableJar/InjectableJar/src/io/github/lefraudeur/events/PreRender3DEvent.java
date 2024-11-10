package io.github.lefraudeur.events;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class PreRender3DEvent extends Event
{
    private WorldRenderer worldRenderer;
    private MatrixStack matrices;
    private float tickDelta;
    private long limitTime;
    private boolean renderBlockOutline;
    private Camera camera;
    private GameRenderer gameRenderer;
    private LightmapTextureManager lightmapTextureManager;
    private Matrix4f projectionMatrix;

    public PreRender3DEvent(WorldRenderer worldRenderer, MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix)
    {
        this.worldRenderer = worldRenderer;
        this.matrices = matrices;
        this.tickDelta = tickDelta;
        this.limitTime = limitTime;
        this.renderBlockOutline = renderBlockOutline;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.lightmapTextureManager = lightmapTextureManager;
        this.projectionMatrix = projectionMatrix;
    }
    @Override
    public void dispatch()
    {
    }
}
