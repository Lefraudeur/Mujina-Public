package io.github.lefraudeur.gui.clickgui.widgets;

import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.github.lefraudeur.Main.mc;

public class Slider extends Component {

    public ValueSetting valueSetting;
    private boolean sliding = false;

    public Slider(SettingBase setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.valueSetting = (ValueSetting) setting;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        drawContext.fill(parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, new Color(0, 0, 0, 60).getRGB());

        double diff = Math.min(parent.parent.width, Math.max(0, mouseX - parent.parent.x));


        if (sliding) {
            if (diff == 0) valueSetting.setValue(valueSetting.getMin());

            else {
                /*
                 * Linear Interpolation (LERP):
                 * LERP smoothly transitions between two values by
                 * linearly interpolating intermediate values based
                 * on a given percentage or fraction.
                 * Here, we calculate the percentage of the slider filled
                 * based on the mouse position, then interpolate between
                 * the minimum and maximum values allowed for valueSetting.
                 * This creates a smooth transition of the slider value
                 * as the mouse moves across the slider area.
                 */

                // Calculate the percentage of the slider filled based on mouse position
                double percentFilled = diff / parent.parent.width;

                // Interpolate between the min and max values based on the percentage filled
                double interpolatedValue = valueSetting.getMin() + percentFilled * (valueSetting.getMax() - valueSetting.getMin());

                // Optionally, you can round the interpolated value to a specific decimal place
                interpolatedValue = roundToPlace(interpolatedValue, valueSetting.decimals);

                // Set the interpolated value
                valueSetting.setValue(interpolatedValue);
            }
        }

        // Use the interpolated value for rendering
        // int renderWidth = (int) (parent.parent.width * (valueSetting.getValue() - valueSetting.getMin() / (valueSetting.getMax() - valueSetting.getMin())));
        int renderWidth = (int) (parent.parent.width * (valueSetting.getValue() - valueSetting.getMin()) / (valueSetting.getMax() - valueSetting.getMin()));
        //double percentFilled = diff / parent.parent.width;

        int actualRenderWidth = MathHelper.clamp(renderWidth, 0, parent.parent.width);

        drawContext.fill(parent.parent.x, parent.parent.y + parent.offset + offset, (int) (parent.parent.x + actualRenderWidth), parent.parent.y + parent.offset + offset + parent.parent.height, new Color(100, 234, 255, 160).getRGB());


        mc.textRenderer.draw(valueSetting.getName() + ": " + valueSetting.getValue(), (float) parent.parent.x + 2, (float) parent.parent.y + parent.offset + offset + 2, 0xE0E0E0, false, matrices.peek().getPositionMatrix(), mc.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        sliding = false;
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            sliding = true;
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
    private double roundToPlace(double value, int place) {
        if (place <0) {
            return value;
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(place, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
