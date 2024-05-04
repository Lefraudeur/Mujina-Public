package io.github.lefraudeur.utils;

import io.github.lefraudeur.utils.player.RotationUtils;
import net.minecraft.block.*;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static io.github.lefraudeur.Main.mc;

public final class BlockUtils {
    public static boolean place(BlockPos pos, Hand hand, RotationPacket packet, Swing swing) {
        if (!canPlace(pos)) return false;
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return false;

        Vec3d vec = Vec3d.ofCenter(pos);

        Direction side = getSide(pos);

        if (side == null)
            return false;

        BlockPos offset = pos.offset(side);
        vec = vec.add((float) side.getOffsetX() * 0.5, (float) side.getOffsetY() * 0.5, (float) side.getOffsetZ() * 0.5);

        BlockHitResult bhr = new BlockHitResult(vec, side.getOpposite(), offset, false);

        switch (packet) {
            case None -> doPlace(bhr, hand, swing);

            case Packet -> {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) RotationUtils.getYaw(vec), (float) RotationUtils.getPitch(vec), mc.player.isOnGround()));
                doPlace(bhr, hand, swing);
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            }

            case Packet2 -> {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (float) RotationUtils.getYaw(vec), (float) RotationUtils.getPitch(vec), mc.player.isOnGround()));
                doPlace(bhr, hand, swing);
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            }
        }

        return true;
    }

    private static void doPlace(BlockHitResult bhr, Hand hand, Swing swing) {
        boolean sneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        if (mc.interactionManager.interactBlock(mc.player, hand, bhr).shouldSwingHand()) {
            switch (swing) {
                case Client -> mc.player.swingHand(hand);
                case Server -> mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
            }
        }

        mc.player.input.sneaking = sneaking;
    }


    public static boolean canPlace(BlockPos blockPos) {
        if (blockPos == null) return false;

        if (!World.isValid(blockPos)) return false;

        if (!mc.world.getBlockState(blockPos).isReplaceable()) return false;

        return mc.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), blockPos, ShapeContext.absent());
    }

    public static Direction getSide(final BlockPos pos) {
        for (Direction side : Direction.values()) {
            BlockState state = mc.world.getBlockState(pos.offset(side));
            if (!state.isAir() && !cantBePlacedOn(state.getBlock()))
                return side;
        }
        return null;
    }

    public static boolean cantBePlacedOn(Block block) {
        return block instanceof CraftingTableBlock
                || block instanceof AnvilBlock
                || block instanceof ButtonBlock
                || block instanceof AbstractPressurePlateBlock
                || block instanceof BlockWithEntity
                || block instanceof BedBlock
                || block instanceof FenceGateBlock
                || block instanceof DoorBlock
                || block instanceof NoteBlock
                || block instanceof TrapdoorBlock;
    }

    public enum Swing {
        None,
        Client,
        Server
    }


    public enum RotationPacket {
        None,
        Packet,
        Packet2
    }
}
