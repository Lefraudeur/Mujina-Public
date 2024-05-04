package io.github.lefraudeur.gui.settings;

import com.google.gson.JsonElement;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.BooleanSupplier;

public abstract class SettingBase {

    protected String description = "";
    private BooleanSupplier visible = () -> true;

    public abstract String getName();

    public BooleanSetting asToggle() {
        try {
            return (BooleanSetting) this;
        }
        catch (final Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public ModeSetting asMode() {
        try {
            return (ModeSetting) this;
        }
        catch (final Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public ValueSetting asSlider() {
        try {
            return (ValueSetting) this;
        }
        catch (final Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }


    public String getDesc() {
        return description;
    }

    public void setVisible(final BooleanSupplier visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible.getAsBoolean();
    }



    public abstract void readSettings(JsonElement settings);

    public abstract JsonElement saveSettings();

    public abstract boolean isDefault();

}
