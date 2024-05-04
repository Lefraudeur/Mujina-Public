package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.misc.Teams;
import io.github.lefraudeur.utils.AuraUtils;
import io.github.lefraudeur.utils.EntityUtils;
import io.github.lefraudeur.utils.MathUtils;
import io.github.lefraudeur.utils.Target;
import io.github.lefraudeur.utils.player.RotationUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;

@Info(category = Category.COMBAT, name = "Aura",  description = "Kills Auras", key = Module.key_none)
public class Aura extends Module {

    private final ModeSetting rotMode = new ModeSetting("Rotations", "The rotation mode.", "packet2", "Vanilla");
    private final BooleanSetting autocrit= new BooleanSetting("AutoCrit", "Autocrit :)))", true);
    private final ValueSetting hit_cooldown = new ValueSetting("cooldown", "The speed you need to go at", 0.912, 0.5, 1, 2);
    private final ValueSetting tickDelay = new ValueSetting("tick delay", "The tick delay", 11, 1, 20, 0);
    private final ValueSetting range = new ValueSetting("range", "The speed you need to go at", 3.0, 0.1, 6.0, 2);
    private final ValueSetting FOV = new ValueSetting("FOV", "The speed you need to go at", 360, 0, 360, 0);
    private final BooleanSetting players = new BooleanSetting("Players", "Whether aura should target players.", true);
    private final BooleanSetting animals = new BooleanSetting("Animals", "Whether aura should target animals.", false);
    private final BooleanSetting monsters = new BooleanSetting("Monsters", "Whether aura should target monsters.", false);
    private final BooleanSetting villagers = new BooleanSetting("Villagers", "Whether aura should target villagers.", false);
    private final BooleanSetting invisible = new BooleanSetting("Invisibles", "Whether aura should target invisible entities.", true);

    private int ticks = 0;
    List<Entity> targets = new ArrayList<>();

    @Override
    protected void onEnable() {
        targets.clear();
        ticks = 0;
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (isNull()) return;

        targets.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (filterEntity(entity)) targets.add(entity);
        }

        if (((AuraUtils.canCrit() && autocrit.get()) || (mc.player.isOnGround() || !autocrit.get())) && mc.player.getAttackCooldownProgress(0.5f) >= hit_cooldown.getValue() && ticks >= tickDelay.getInt()) {
            //attack(targets.get(0));
            targets.forEach(this::attack);
            ticks = 0;
        }

        ticks++;
    }

    private void attack(final Entity entity) {
        // TODO: rotations

        rotate(true, entity);

        mc.interactionManager.attackEntity(mc.player, entity);
        mc.player.swingHand(Hand.MAIN_HAND);

        rotate(false, entity);
    }

    private void rotate(final boolean preOrPost, Entity entity) {
        switch (rotMode.get()) {
            case "packet2":
            if (preOrPost)
                send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (float) RotationUtils.getYaw(entity), (float) RotationUtils.getPitch(entity, Target.Head), mc.player.isOnGround()));
            else
                send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            break;

            case "vanilla":
                send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (float) RotationUtils.getYaw(entity), (float) RotationUtils.getPitch(entity, Target.Head), mc.player.isOnGround()));
                break;

            default:
                break;
        }
    }

    private boolean filterEntity(final Entity entity) {
        if (!(entity instanceof LivingEntity)) return false;
        if (entity instanceof ClientPlayerEntity) return false;
        if (entity instanceof ArmorStandEntity) return false;
        if (entity.isInvulnerable()) return false;
        if (entity.age == 0) return false;
        if (!entity.isAlive()) return false;

        if (!MathUtils.isInFOV(entity, FOV.getValue())) return false;

        if (entity.distanceTo(mc.player) > range.getValue()) return false;

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

    // useless for now
    private void getDistanceTo(final Entity entity) {
    }
}