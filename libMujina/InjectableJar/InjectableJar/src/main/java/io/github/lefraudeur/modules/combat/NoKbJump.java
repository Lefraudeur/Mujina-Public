package io.github.lefraudeur.modules.combat;

import io.github.lefraudeur.events.PreTickEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MathUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Info(category = Category.COMBAT, name = "NoKbJump",  description = "Jumps when you are hit", key = Module.key_none)
public final class NoKbJump extends Module {

    private final BooleanSetting chanceBool = new BooleanSetting("chance", "", true);

    private final ValueSetting percentage = new ValueSetting("percentage", "", 80, 0, 100, 0);

    private final ValueSetting ticks = new ValueSetting("ticks", "The speed you need to go at", 6, 1, 12.0, 0);

    @Override
    protected void onEnable() {
        //mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, 1, 0));
        //this.toggle();
    }

    private boolean isInCombat() {
        assert mc.world != null;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.getAbilities().creativeMode || player == mc.player || !player.isAlive()) continue;
            //if (!Friends.get().shouldAttack(player)) continue;
            if (player.distanceTo(mc.player) <= 6) return true;
        }
        return false;
    }

    @Override
    public void onPreTickEvent(final PreTickEvent event) {
        if (mc.currentScreen != null || isNull() || mc.player == null || mc.player.isBlocking() || mc.player.isUsingItem()
                || !mc.player.isSprinting() || !(mc.player.getAttacker() instanceof PlayerEntity) || mc.player.isOnFire()
                || mc.currentScreen instanceof HandledScreen || mc.player.isTouchingWater() || mc.player.isInsideWall())
                    return;


        MathHelper.nextInt(Random.create(), 1, 100);
        if (mc.player.hurtTime > 6 && mc.player.isOnGround() && !mc.player.isOnFire() && isInCombat() && !chanceBool.get())
            mc.player.jump();

        if (chanceBool.get() && mc.player.hurtTime > ticks.getInt() && mc.player.isOnGround() && !mc.player.isOnFire() && MathUtils.chance(0, 100, percentage.getValue()))
            mc.player.jump();
    }
}
