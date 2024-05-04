package io.github.lefraudeur.gui.settings.types;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.SettingBase;
import net.minecraft.client.util.math.MatrixStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BooleanSupplier;

public class ValueSetting extends SettingBase {

    public double min;

    public double max;

    private double value;

    public int decimals;

    public String text;

    protected double defaultValue;

    public ValueSetting(final String name, final String desc, final double value, final double min, final double max, final int decimals) {
        description = desc;
        this.min = min;
        this.max = max;
        this.value = value;
        this.decimals = decimals;
        text = name;

        defaultValue = value;
    }

    public ValueSetting(final String name, final String desc, final double value, final double min, final double max, final int decimals, BooleanSupplier visible) {
        description = desc;
        this.min = min;
        this.max = max;
        this.value = value;
        this.decimals = decimals;
        text = name;
        this.setVisible(visible);

        defaultValue = value;
    }

    public double getValue() {
        return round(value, decimals);
    }

    public float getFloat() {
        return (float) getValue();
    }

    public int getInt() {
        return (int) getValue();
    }

    public long getLong() {
        return (long) getValue();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void setValue(final double value) {
        this.value = value;
        // for settings, potentially
        // FileHelper.SCHEDULE_SAVE_HACKS = true;
    }

    public double round(final double value, final int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public String getName() {
        return text;
    }

    public void render(final ClickGUI window, final MatrixStack matrices, final int x, final int y, final int len) {

    }

    public ValueSetting withDesc(final String desc) {
        description = desc;
        return this;
    }



    @Override
    public void readSettings(final JsonElement settings) {
        if(settings.isJsonPrimitive()) setValue(settings.getAsDouble());
    }

    @Override
    public JsonElement saveSettings() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public boolean isDefault() {
        BigDecimal bd = new BigDecimal(defaultValue);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);

        return bd.doubleValue() == getValue();
    }
}