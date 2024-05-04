package io.github.lefraudeur.gui.settings.types;

import com.google.gson.JsonElement;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.SettingBase;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

public class BooleanSetting extends SettingBase {

    protected boolean state;

    protected String name;
    protected boolean defaultState;

    protected final ArrayList<SettingBase> children = new ArrayList<>();

    protected boolean expanded = false;

    public BooleanSetting(final String name, final String desc, final boolean defaultState, BooleanSupplier visible) {
        description = desc;
        this.state = defaultState;
        this.name = name;
        setVisible(visible);
        // defaultState = state;
    }

    public BooleanSetting(final String name, final String desc, final boolean defaultState) {
        description = desc;
        this.state = defaultState;
        this.name = name;
        // defaultState = state;
    }

    public boolean get() {
  	    return state;
    }

    public void setValue(final boolean state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    public SettingBase getChild(final int c) {
        return children.get(c);
    }

    public BooleanSetting withChildren(final SettingBase... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public BooleanSetting withDesc(final String desc) {
        description = desc;
        return this;
    }

    @Override
    public void readSettings(JsonElement settings) {

    }

    @Override
    public JsonElement saveSettings() {
        return null;
    }

    @Override
    public boolean isDefault() {
        if(state != defaultState) return false;

        for(final SettingBase s : children) if(!s.isDefault()) return false;

        return true;
    }

    public ArrayList<SettingBase> getChildren() {
        return children;
    }

}
