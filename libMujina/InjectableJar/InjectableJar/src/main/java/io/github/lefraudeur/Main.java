package io.github.lefraudeur;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.modules.combat.*;
import io.github.lefraudeur.modules.exploit.Disabler;
import io.github.lefraudeur.modules.exploit.ServerCrasher;
import io.github.lefraudeur.modules.exploit.Timer;
import io.github.lefraudeur.modules.exploit.timer2;
import io.github.lefraudeur.modules.misc.*;
import io.github.lefraudeur.modules.movement.*;
import io.github.lefraudeur.modules.player.FastBreak;
import io.github.lefraudeur.modules.player.FastUse;
import io.github.lefraudeur.modules.player.NoFall;
import io.github.lefraudeur.modules.player.Scaffold;
import io.github.lefraudeur.modules.visual.ESP;
import io.github.lefraudeur.modules.visual.GUI;
import io.github.lefraudeur.modules.visual.HUD;
import io.github.lefraudeur.modules.visual.Particles;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Main
{
    public final static MinecraftClient mc = MinecraftClient.getInstance();
    public final static String clientVersion = "1.0";
    
    public final static Module[] modules = new Module[] {
            // Combat category
            new Aura(),
            new AimAssist(),
            new TriggerBot(),
            new Velocity(),
            new NoMiss(),
            new wTap(),
            new NoKbJump(),
            new Reach(),
            // Movement Category.
            new AutoJump(),
            new Blink(),
            new Flight(),
            new Glide(),
            new HighJump(),
            new Clip(),
            new Sprint(),
            new Speed(),

            // Exploit Category
            new Disabler(),
            new ServerCrasher(),
            new Timer(),
            new timer2(),
            // The Player Category
            new Scaffold(),
            new FastUse(),
            new NoFall(),
            new FastBreak(),

            // The Misc and cool utility Category.
            new AutoGG(),
            new CivBreak(),
            new Panic(),
            new Rocket(),
            new Teams(),
            new TestModuul(),
            // The Visual Category
            new GUI(),
            new HUD(),
            new ESP(),
            new Particles()
    };
    //called in c++ code
    public static void init() {
        for (Module module : modules) {
            module.registerSettings();
        }
    }
    public static void shutdown() //hardcoded in c++ code
    {
        for (Module module : modules) {
            if (module instanceof GUI) {
                while (module.isEnabled());
                continue;
            }
            module.disable();
        }
    }

    public static Module[] getEnabledModules() {
        return Arrays.stream(modules).filter(Module::isEnabled).toArray(Module[]::new);
    }
    public static Module[] getEnabledSortedModules() {
        return Arrays.stream(getEnabledModules())
                .sorted(Comparator.comparingInt((Module module) -> mc.textRenderer.getWidth(module.getName())).reversed())
                .toArray(Module[]::new);
    }

    @Nullable
    public static Module getModuleByClass(Class<? extends Module> moduleType) {
        for (Module module : modules)
            if (module.getClass() == moduleType)
                return module;
        return null;
    }
    public static Category getCategoryByName(final String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) return module.getCategory();
        }
        return null;
    }
}
