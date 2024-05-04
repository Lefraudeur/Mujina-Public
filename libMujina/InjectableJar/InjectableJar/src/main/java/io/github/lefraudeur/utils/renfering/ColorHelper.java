package io.github.lefraudeur.utils.renfering;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.modules.Category;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Random;

public class ColorHelper {

    public static Formatting getLatencyColor(final int ms) {
        final int ping = ms;
        Formatting latencyColour = Formatting.WHITE;

        if(ping >= 350) latencyColour = Formatting.DARK_RED;

        else if(ping > 300) latencyColour = Formatting.RED;

        else if(ping >= 180) latencyColour = Formatting.GOLD;

        else if(ping >= 80) latencyColour = Formatting.YELLOW;

        else if(ping >= 40) latencyColour = Formatting.GREEN;

        else if(ping > 0) latencyColour = Formatting.DARK_GREEN;
        return latencyColour;
    }

    public static int getUtilityRGB(final String name) {
        final Category colorChoose = Main.getCategoryByName(name);

        int hackColor = 0xffff8400;

        if(colorChoose.equals(Category.COMBAT)) hackColor = 0xfffc0303;

        else if(colorChoose.equals(Category.MOVEMENT)) hackColor = 0xff00ddff;

        else if(colorChoose.equals(Category.MISC)) hackColor = 0xff42ffd6;

        else if(colorChoose.equals(Category.EXPLOIT)) hackColor = 0xffc203fc;

        else if(colorChoose.equals(Category.CRYSTAL)) hackColor = 0xff03cc00;

        else if(colorChoose.equals(Category.PLAYER)) hackColor = 0xffff00c3;

        else if(colorChoose.equals(Category.VISUAL)) hackColor = 0xfffffb00;

        return hackColor;
    }

    public static int getCategoryRGB(final Category category) {

        int hackColor = 0xffff8400;

        if(category.equals(Category.COMBAT)) hackColor = 0xfffc0303;

        else if(category.equals(Category.MOVEMENT)) hackColor = 0xff00ddff;

        else if(category.equals(Category.MISC)) hackColor = 0xff42ffd6;

        else if(category.equals(Category.EXPLOIT)) hackColor = 0xffc203fc;

        else if(category.equals(Category.CRYSTAL)) hackColor = 0xff03cc00;

        else if(category.equals(Category.PLAYER)) hackColor = 0xffff00c3;

        else if(category.equals(Category.VISUAL)) hackColor = 0xff00ff00;

        return hackColor;
    }


    public static int rainBowSimple() {
        float hue = System.nanoTime() / 1.0E10f % 1.0f;
        hue = hue + 0.6f;
        final int color = Color.HSBtoRGB(hue, 1, 1);
        return new Color(color).getRGB();
    }

    public static Color rainBowSimpleColor()
    {
        float hue = System.nanoTime() / 1.0E10f % 1.0f;
        hue = hue + 0.6f;
        final int color = Color.HSBtoRGB(hue, 1, 1);
        return new Color(color);
    }

    public static int rainBowFast(final int delay, final float saturation, final float brightness)
    {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 4L);
        rainbowState %= 360.0D;
        return Color.getHSBColor((float) (rainbowState / 360.0D), saturation, brightness).getRGB();
    }

    public static Color darker(final Color color, final double fraction)
    {
        int red = (int) Math.round(color.getRed() * (1.0 - fraction));
        int green = (int) Math.round(color.getGreen() * (1.0 - fraction));
        int blue = (int) Math.round(color.getBlue() * (1.0 - fraction));

        if(red < 0) red = 0;
        else if(red > 255) red = 255;

        if(green < 0) green = 0;
        else if(green > 255) green = 255;

        if(blue < 0) blue = 0;
        else if(blue > 255) blue = 255;
        final int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

    public static Color lighter(final Color color, final double fraction)
    {
        int red = (int) Math.round(color.getRed() * (1.0 + fraction));
        int green = (int) Math.round(color.getGreen() * (1.0 + fraction));
        int blue = (int) Math.round(color.getBlue() * (1.0 + fraction));

        if(red < 0) red = 0;
        else if(red > 255) red = 255;

        if(green < 0) green = 0;
        else if(green > 255) green = 255;

        if(blue < 0) blue = 0;
        else if(blue > 255) blue = 255;
        final int alpha = color.getAlpha();
        return new Color(red, green, blue, alpha);
    }

    public static Color blendColors(final float[] fractions, final Color[] Colors, final float progress)
    {
        if(fractions == null) throw new IllegalArgumentException("Fractions can't be null");

        if(Colors == null) throw new IllegalArgumentException("Colours can't be null");

        if(fractions.length != Colors.length) throw new IllegalArgumentException("Fractions and colours must have equal number of elements");

        final int[] indicies = getFractionIndicies(fractions, progress);

        final float[] range = new float[]
                { fractions[indicies[0]], fractions[indicies[1]] };

        final Color[] colorRange = new Color[]
                { Colors[indicies[0]], Colors[indicies[1]] };

        final float max = range[1] - range[0];
        final float value = progress - range[0];
        final float weight = value / max;
        return blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    public static Color fadeWithOpacity(final Color color, final float delay, final int opacity) {
        // Convert color to HSB (Hue, Saturation, Brightness) color space
        final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        // Calculate brightness based on the current time and delay
        float brightness = Math.abs(System.currentTimeMillis() % 2000L / delay % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;

        // Update brightness component in HSB
        hsb[2] = brightness;

        // Convert back to RGB and create a new Color object with the given opacity
        int red = Math.min((int)(hsb[0] * 255), 255);
        int green = Math.min((int)(hsb[1] * 255), 255);
        int blue = Math.min((int)(hsb[2] * 255), 255);
        int alpha = (int) (color.getAlpha() * opacity);
        alpha = Math.min(opacity, 255); // Ensure alpha is within valid range [0, 255]

        return new Color(red, green, blue, alpha);
    }

    public static int[] getFractionIndicies(final float[] fractions, final float progress)
    {
        int startPoint = 0;

        // Find the starting index where progress falls between fractions
        while (startPoint < fractions.length && fractions[startPoint] <= progress) {
            startPoint++;
        }

        // Adjust startPoint if it exceeds the array bounds
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }

        // Define the range based on the starting point
        return new int[]{startPoint - 1, startPoint};
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        // Ensure ratio is within the valid range [0, 1]
        final float r = (float) Math.max(0, Math.min(1, ratio));
        final float ir = 1.0f - r;

        final float[] rgb1 = color1.getColorComponents(null);
        final float[] rgb2 = color2.getColorComponents(null);

        // Blend the RGB components
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;

        // Ensure RGB components are within valid range [0, 255]
        red = Math.max(0.0f, Math.min(255.0f, red));
        green = Math.max(0.0f, Math.min(255.0f, green));
        blue = Math.max(0.0f, Math.min(255.0f, blue));

        // Create and return the blended color
        return new Color((int) red, (int) green, (int) blue);
    }

    public static Color fade(final Color color) {
        return fade(color, 1000.0F);
    }

    public static Color fade(final Color color, final float delay) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(System.currentTimeMillis() % 2000L / delay % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color fade(final Color color, final float delay, final int opacity) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(System.currentTimeMillis() % 2000L / delay % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return new Color(red, green, blue, opacity );
    }



    public static Color fadeWithMaxBrightness(final Color color, final float delay, final float maxBrightness) {
        // Convert color to HSB (Hue, Saturation, Brightness) color space
        final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        // Calculate brightness based on the current time and delay
        float brightness = Math.abs(System.currentTimeMillis() % 2000L / delay % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;

        // Apply max brightness limit
        brightness = Math.min(brightness, maxBrightness);

        // Update brightness component in HSB
        hsb[2] = brightness;

        // Convert back to RGB and create a new Color object
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }


    public static Color alphaIntegrate(final Color color, final float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }


    public static int getRandomColor()
    {
        final char[] letters = "012345678".toCharArray();
        final StringBuilder color = new StringBuilder("0x");

        for(int i = 0; i < 6; ++i) color.append(letters[new Random().nextInt(letters.length)]);

        return Integer.decode(color.toString());
    }
}
