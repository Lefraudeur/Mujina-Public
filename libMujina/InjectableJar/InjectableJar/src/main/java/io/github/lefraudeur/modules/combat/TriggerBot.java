package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.misc.Teams;
import io.github.lefraudeur.utils.EntityUtils;
import io.github.lefraudeur.utils.FindItemResult;
import io.github.lefraudeur.utils.MoveUtils;
import io.github.lefraudeur.utils.player.InvUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.function.Predicate;

import static io.github.lefraudeur.modules.Module.key_none;

@Info(category = Category.COMBAT, name = "TBot",  description = "Hits people :)", key = key_none)
public final class TriggerBot extends Module {

    private final ModeSetting weaponType = new ModeSetting("Weapon", "What weapon should aura prefer?", "Sword", "Axe", "Pickaxe", "Slot", "All", "Any");
    private final ValueSetting hit_cooldown = new ValueSetting("Hit Cooldown", "The max cooldown before attacking an entity.", 0.912, 0.8, 1, 2);
    private final ValueSetting tickDelay = new ValueSetting("tick delay", "The tick delay", 11, 1, 20, 0);
    private final ValueSetting slotNumber = new ValueSetting("SlotNumber", "The slot it should be", 1, 1, 9, 0);
    private final BooleanSetting smartSprint = new BooleanSetting("SprintPredict", "Stops sprinting when attacking while being midair to increase chances of dealing critical hits.", true);
    private final BooleanSetting wTap = new BooleanSetting("wTap ", "wTaps", true);
    private final BooleanSetting sendSprint = new BooleanSetting("ResendSprint", "Does a funny and sends the opposite packet to the packet you sent", true);
    private final BooleanSetting hitAnim = new BooleanSetting("Hit Animation", "Should you have an animation when hitting", true);
    private final BooleanSetting breakShield = new BooleanSetting("Break the shields", "Break shields in 1 tick", true);
    private final BooleanSetting players = new BooleanSetting("Players", "Whether aura should target players.", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Whether aura should target animals.", false);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", "Whether aura should target monsters.", false);
    private final BooleanSetting villagers = new BooleanSetting("Villagers", "Whether aura should target villagers.", false);
    private final BooleanSetting invisible = new BooleanSetting("Invisibles", "Whether aura should target invisible entities.", true);

    private int ticks = 0;

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player.isBlocking() || mc.player.isUsingItem()) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType().equals(HitResult.Type.ENTITY) && mc.crosshairTarget instanceof EntityHitResult && ticks >= tickDelay.getInt()) {
            final EntityHitResult entity = (EntityHitResult) mc.crosshairTarget;
            if ((isItemInHand() && isValidTarget(entity.getEntity())) && (mc.player.isOnGround() || canCrit())) {
                attack(entity.getEntity());
                ticks = 0;
            }
        }
        ticks++;
    }

    private void attack(final Entity entity) {
        if (isNull() || entity == null) return;
        Entity target = mc.targetedEntity;
        Predicate<ItemStack> predicate = stack -> stack.getItem() instanceof AxeItem;
        FindItemResult weaponResult = InvUtils.findInHotbar(predicate);

        assert target != null;
        if (target.isPlayer() && shouldBreakShield((PlayerEntity) target) && breakShield.get()) {
            assert mc.player != null;
            int lastSlot = mc.player.getInventory().selectedSlot;
            FindItemResult axeResult = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof AxeItem);
            if (axeResult.found()) weaponResult = axeResult;
            InvUtils.swap(weaponResult.slot(), false);

            assert mc.interactionManager != null;
            mc.interactionManager.attackEntity(mc.player, entity);
            mc.player.swingHand(Hand.MAIN_HAND);

            mc.interactionManager.attackEntity(mc.player, entity);
            mc.player.swingHand(Hand.MAIN_HAND);

            InvUtils.swap(lastSlot, false);
            return;
        }

        if (!mc.player.isOnGround() && MoveUtils.hasMovement() && smartSprint.get()) {
            send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        if (mc.player.isOnGround() && wTap.get()) {
            send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }

        if (mc.player.getAttackCooldownProgress(0.5f) >= hit_cooldown.getValue()) {
            mc.interactionManager.attackEntity(mc.player, entity);
            if (hitAnim.get()) mc.player.swingHand(Hand.MAIN_HAND);
            if (sendSprint.get()) {
                if (smartSprint.get())
                    send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                if (wTap.get()) {
                    send(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    mc.player.setSprinting(true);
                }
            }
        }
    }

    private boolean isValidTarget(final Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;

        // uncomment this if you want to do testing
        // return true;

        // if the crossHairTarget is a player and you haven't selected players as targets (in your settings)
        if (!players.get() && entity instanceof PlayerEntity) {
            return false;
        }
        if (!animals.get() && EntityUtils.isAnimal(entity)) {
            return false;
        }
        if (!monsters.get() && entity instanceof MobEntity) {
            return false;
        }
        if (!villagers.get() && entity instanceof VillagerEntity) {
            return false;
        }
        if (!invisible.get() && (entity.isInvisible() || entity.isInvisibleTo(mc.player))) {
            return false;
        }
        return !Teams.isTeam(entity);
    }

    public static boolean canCrit() {
        return (!mc.player.isOnGround() && MoveUtils.hasMovement() && mc.player.fallDistance >= 0.065 && mc.player.getAttackCooldownProgress(0.5f) >= 0.86)
                || (hasFlyUtils());
    }

    public static boolean hasFlyUtils() {
        return mc.player.getAbilities().flying;
    }

    public static boolean shouldBreakShield(final PlayerEntity player) {
        return player.blockedByShield(mc.world.getDamageSources().playerAttack(mc.player)); // && disableShields.get();
    }


    private boolean isRightSlot() {
        return mc.player.getInventory().selectedSlot == slotNumber.getInt() - 1;
    }

    private boolean isItemInHand() {
        if (isNull()) return false;
        final Item item = mc.player.getMainHandStack().getItem();

        return switch (weaponType.get()) {
            case "Sword" -> item instanceof SwordItem;
            case "Axe" -> item instanceof AxeItem;
            case "Pickaxe" -> item instanceof PickaxeItem;
            case "Slot" -> isRightSlot();
            case "All" -> item instanceof SwordItem || item instanceof AxeItem || item instanceof PickaxeItem;
            case "Any" -> true;
            default -> false;
        };
    }
}
