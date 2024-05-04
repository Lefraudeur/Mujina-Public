package io.github.lefraudeur.gui.clickgui.widgets;

import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;

import static io.github.lefraudeur.Main.mc;

public class ModeBox extends Component {

    private final ModeSetting modeSetting = (ModeSetting) setting;
    public ModeBox(SettingBase setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        drawContext.fill(parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, new Color(0, 0, 0, 60).getRGB());

        mc.textRenderer.draw(modeSetting.getName() + ": " + modeSetting.get(), (float) parent.parent.x + 2, (float) parent.parent.y + parent.offset + offset + 2, 0xE0E0E0, false, matrices.peek().getPositionMatrix(), mc.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0 && parent.extended) {
            modeSetting.getNextMode();
            super.parent.parent.updateButtons();
        }
        if (isHovered(mouseX, mouseY) && button == 1 && parent.extended) {
            modeSetting.getPrevMode();
            super.parent.parent.updateButtons();
        }
        parent.parent.updateButtons();
        if (isHovered(mouseX, mouseY)) mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        super.mouseClicked(mouseX, mouseY, button);
    }
}
