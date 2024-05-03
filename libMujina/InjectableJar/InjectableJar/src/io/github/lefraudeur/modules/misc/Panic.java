package io.github.lefraudeur.modules.misc;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;

@Info(category = Category.MISC, name = "Panic",  description = "Disables all modules when enabled :)", key = Module.key_none)
public final class Panic extends Module {

    @Override
    protected void onEnable() {
        if (isNull() || mc.interactionManager == null || !this.isEnabled()) return;

        for (Module modules : Main.modules) {
            modules.disable();
        }
        this.disable();
    }
}
