package io.github.lefraudeur.gui.settings.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.util.math.MatrixStack;

public class BindSetting extends SettingBase {

    private final Module mod;

    public BindSetting(final Module mod) {
        this.mod = mod;
    }

    @Override
    public String getName() {
        return "Bind";
    }

    public BindSetting withDesc(final String desc) {
        description = desc;
        return this;
    }

    public Module getMod() {
        return mod;
    }

    @Override
    public void readSettings(JsonElement settings) {

    }

    @Override
    public JsonElement saveSettings() {
        return new JsonPrimitive(mod.getKeyBind());
    }



    @Override
    public boolean isDefault() {
        return mod.getKeyBind() == mod.getDefaultKey() || mod.getDefaultKey() >= 0;
    }


}
