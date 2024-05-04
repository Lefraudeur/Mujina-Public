package io.github.lefraudeur.gui.settings.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lefraudeur.gui.clickgui.ClickGUI;
import io.github.lefraudeur.gui.settings.SettingBase;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ColorSetting extends SettingBase {

    public String text;

    public float hue, r;

    public float sat, g;

    public float bri, b;

    protected float defaultHue;

    protected float defaultSat;

    protected float defaultBri;

    public ColorSetting(final String text, final String desc, final float r, final float g, final float b, final boolean hsv) {
        this.text = text;
        description = desc;

        final float[] vals = rgbToHsv(r, g, b);
        final float[] color = getRGBFloat();

        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        hue = hsv ? r : vals[0];
        sat = hsv ? g : vals[1];
        bri = hsv ? b : vals[2];

        defaultHue = hue;
        defaultSat = sat;
        defaultBri = bri;
    }

    @Override
    public String getName()
    {
        return text;
    }



    public void setHue(float hue)
    {
        this.hue = hue;
    }

    public void setSat(float sat)
    {
        this.sat = sat;
    }

    public void setBri(float bri)
    {
        this.bri = bri;
    }

    public ColorSetting withDesc(final String desc)
    {
        description = desc;
        return this;
    }

    public Vec3d getVec3d()
    {
        // System.out.println(defaultHue + " " + defaultSat + " " + defaultBri);
        return new Vec3d(1, 0, 0);
    }

    public int getPacked()
    {
        return MathHelper.hsvToRgb(hue, sat, bri);
    }

    public java.awt.Color getColor()
    {
        return new java.awt.Color(255 * hue, 255 * sat, 255 * bri, 255.0F);
    }

    @Override
    public void readSettings(final JsonElement settings)
    {

        if(settings.isJsonObject())
        {
            final JsonObject jo = settings.getAsJsonObject();
            hue = jo.get("hue").getAsFloat();
            sat = jo.get("sat").getAsFloat();
            bri = jo.get("bri").getAsFloat();
        }
    }

    @Override
    public JsonElement saveSettings()
    {
        final JsonObject jo = new JsonObject();
        jo.add("hue", new JsonPrimitive(hue));
        jo.add("sat", new JsonPrimitive(sat));
        jo.add("bri", new JsonPrimitive(bri));

        return jo;
    }

    public String getText()
    {
        return text;
    }

    public float getHue()
    {
        return hue;
    }

    public float getSat()
    {
        return sat;
    }

    public float getBri()
    {
        return bri;
    }

    public int getRGB()
    {
        return MathHelper.hsvToRgb(hue, sat, bri);
    }

    public float[] getRGBFloat()
    {
        final int col = MathHelper.hsvToRgb(hue, sat, bri);
        return new float[]
                { (col >> 16 & 255) / 255f, (col >> 8 & 255) / 255f, (col & 255) / 255f };
    }

    @Override
    public boolean isDefault()
    {
        return hue == defaultHue && sat == defaultSat && bri == defaultBri;
    }

    private float[] rgbToHsv(final float r, final float g, final float b)
    {
        final float minRGB = Math.min(r, Math.min(g, b));
        final float maxRGB = Math.max(r, Math.max(g, b));

        // Black-gray-white
        if(minRGB == maxRGB) return new float[]
                { 0f, 0f, minRGB };

        // Colors other than black-gray-white:
        final float d = r == minRGB ? g - b : b == minRGB ? r - g : b - r;
        final float h = r == minRGB ? 3 : b == minRGB ? 1 : 5;
        final float computedH = 60 * (h - d / (maxRGB - minRGB)) / 360f;
        final float computedS = (maxRGB - minRGB) / maxRGB;

        return new float[]
                { computedH, computedS, maxRGB};
    }
}