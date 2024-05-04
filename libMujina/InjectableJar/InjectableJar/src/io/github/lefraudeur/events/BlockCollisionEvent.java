package io.github.lefraudeur.events;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class BlockCollisionEvent extends Event
{
    private BlockState blockState;
    private BlockView world;
    private BlockPos pos;
    private ShapeContext context;
    private VoxelShape returnValue = null;
    public BlockCollisionEvent(BlockState blockState, BlockView world, BlockPos pos, ShapeContext context)
    {
        this.blockState = blockState;
        this.world = world;
        this.pos = pos;
        this.context = context;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public BlockView getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ShapeContext getContext() {
        return context;
    }

    public void setBlockState(BlockState blockState)
    {
        this.blockState = blockState;
    }
    public void setWorld(BlockView world)
    {
        this.world = world;
    }
    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }
    public void setContext(ShapeContext context)
    {
        this.context = context;
    }

    @Override
    public void dispatch() {
        for (Module module : Main.modules)
            if (module.isEnabled())
                module.onBlockCollisionEvent(this);
    }

    public void cancel(VoxelShape returnValue)
    {
        this.returnValue = returnValue;
        this.setCancelled(true);
    }

    public VoxelShape getReturnValue()
    {
        return returnValue;
    }
}
