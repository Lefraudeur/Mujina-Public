package io.github.lefraudeur.gui.clickgui.widgets;

import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.BindSetting;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static io.github.lefraudeur.Main.mc;

public class BindButton extends Component {

    BindSetting bindSetting;
    private boolean shouldGetKey = false;

    public BindButton(SettingBase setting, ModuleButton parent, int offset) {
        super(setting, parent, offset);
        this.bindSetting = (BindSetting) setting;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        drawContext.fill(parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, new Color(0, 0, 0, 60).getRGB());

        if (isHovered(mouseX, mouseY)) {
            drawContext.fill(parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, 0x70303070);
        }

        String name = bindSetting.getMod().getKeyBind() < 0 ? "None" : InputUtil.fromKeyCode(bindSetting.getMod().getKeyBind(), -1).getLocalizedText().getString();

        mc.textRenderer.draw((shouldGetKey ? "..." : "Bind: " + name), (float) parent.parent.x + 2, (float) parent.parent.y + parent.offset + offset, 0xcfe0cf, false, matrices.peek().getPositionMatrix(), mc.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (shouldGetKey && parent.extended) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE) {
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                bindSetting.getMod().setKey(Module.key_none);
            }

            else bindSetting.getMod().setKey(keyCode);
            this.shouldGetKey = false;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0 && parent.extended) {
            shouldGetKey = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}
