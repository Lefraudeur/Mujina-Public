package io.github.lefraudeur.modules.misc;

import io.github.lefraudeur.events.MidUpdateTargetedEntityEvent;
import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@Info(category = Category.MISC, name = "TestModuul",  description = "tests for you...", key = Module.key_none)
public final class TestModuul extends Module
{
    private final  BooleanSetting testBool = new BooleanSetting("Test Bool!!", "aaa", true);
    private final  BooleanSetting testBool2 = new BooleanSetting("Test Bool2!!", "aaa", false);
    private final ValueSetting velocity = new ValueSetting("Velocity", "Changes velocity", 1, 0.1, 20, 2);
    private final  ModeSetting modeTest = new ModeSetting("TestMode", "Tests modes...", "A", "B", "C", "D");

    @Override
    protected void onEnable()
    {

        StringBuilder moduleList = new StringBuilder();
        for (SettingBase setting : getSettings()) {
            if (setting != null)
                moduleList.append("- ").append(setting.toString()).append('\n');
        }
        send(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) mc.crosshairTarget, 1));
        message("The current settings registered are: \n" + moduleList);
        mc.player.setVelocity(MinecraftClient.getInstance().player.getVelocity().add(0, velocity.getValue(), 0));
        disable();
    }

    @Override
    public void onMidUpdateTargetedEntityEvent(MidUpdateTargetedEntityEvent event) {

        //event.getGameRenderer().
    }
}