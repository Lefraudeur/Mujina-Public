package io.github.lefraudeur.gui.settings.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.SettingBase;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends SettingBase {

    public String name;
    private List<String> modes = new ArrayList<>();
    private String value;

    public ModeSetting(final String text, final String desc, final String... modes) {
        name = text;
        description = desc;
        this.modes = Arrays.asList(modes);
        value = this.modes.get(0);
    }

    public void getNextMode() {
        int currentIndex = getIndexOfValue();
        if (currentIndex == getModes().size() - 1)
        {
            currentIndex = 0;
        }
        else
        {
            currentIndex++;
        }
        setValue(getModes().get(currentIndex));
    }

    public void getPrevMode()
    {
        int currentIndex = getIndexOfValue();
        if (currentIndex == 0)
        {
            currentIndex = getModes().size() - 1;
        }
        else
        {
            currentIndex--;
        }
        setValue(getModes().get(currentIndex));
    }

    public int getIndexOfValue()
    {
        return getModes().indexOf(get());
    }

    public List<String> getModes()
    {
        return modes;
    }

    public String get()
    {
        return value;
    }

    public void setValue(final String value)
    {
        this.value = value;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void render(final ClickGUI window, final MatrixStack matrices, final int x, final int y, final int len)
    {

    }

    public ModeSetting withDesc(final String desc)
    {
        description = desc;
        return this;
    }

    @Override
    public void readSettings(final JsonElement settings)
    {
        if (settings.isJsonPrimitive())
        {
            setValue(getModes().get(settings.getAsInt()));
        }
    }

    @Override
    public JsonElement saveSettings() {
        return new JsonPrimitive(getModes().indexOf(get()));
    }

    @Override
    public boolean isDefault() {
        return get() == getModes().get(0);
    }

    public boolean isMode(String mode) {
        return get().equalsIgnoreCase(mode);
    }
}