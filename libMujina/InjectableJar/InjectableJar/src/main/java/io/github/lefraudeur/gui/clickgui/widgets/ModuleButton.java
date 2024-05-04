package io.github.lefraudeur.gui.clickgui.widgets;

import io.github.lefraudeur.gui.clickgui.Frame;
import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.BindSetting;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.gui.settings.types.ValueSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.github.lefraudeur.Main.mc;

public class ModuleButton {

    public Category category;
    public Module module;
    public Frame parent;
    public int offset;
    public List<Component> components;
    public boolean extended;

    public ModuleButton(Module module, Frame parent, int offset) {
        this.module = module;
        this.parent = parent;
        this.offset = offset;
        this.extended = false;
        this.components = new ArrayList<>();

        int setOffset = parent.height;
        for (SettingBase settingBase : module.getSettings()) {
            if (settingBase instanceof BooleanSetting) components.add(new CheckBox(settingBase, this, setOffset));
            else if (settingBase instanceof ModeSetting) components.add(new ModeBox(settingBase, this, setOffset));
            else if (settingBase instanceof ValueSetting) components.add(new Slider(settingBase, this, setOffset));
            else if(settingBase instanceof BindSetting) components.add(new BindButton(settingBase, this, setOffset));
            setOffset += parent.height;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate);
        drawContext.fill(parent.x, parent.y + offset, parent.x + parent.width, parent.y + offset + parent.height, new Color(0, 0, 0, 160).getRGB());
        if (isHovered(mouseX, mouseY)) {
            drawContext.fill(parent.x, parent.y + offset, parent.x + parent.width, parent.y + offset + parent.height, new Color(0, 0, 0, 100).getRGB());
        }
        mc.textRenderer.draw(module.getName(), (float) parent.x + 2, (float) parent.y + offset + 2, (module.isEnabled() ? 0x00FF00 : 0xE0E0E0), false, matrices.peek().getPositionMatrix(), mc.getBufferBuilders().getEntityVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        if (extended) {
            for (Component component : components) {
                if (component.setting.isVisible())
                    component.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

   public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            // if left click, toggle the module the button is associated with
            if (button == 0 && parent.extended) {
                module.toggle();
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            } else if (button == 1 && parent.extended) {
                // DONE: Make module buttons extendable (after settings are done)
                extended = !extended;
                parent.updateButtons();
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }

       if (extended) {
           for (Component component : components) {
               if (component.setting.isVisible()) component.mouseClicked(mouseX, mouseY, button);
           }
       }


   }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (extended) {
            for (Component component : components) {
                if (component.setting.isVisible()) component.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (extended) {
            for (Component component : components) {
                if (component.setting.isVisible()) component.keyPressed(keyCode, scanCode, modifiers);
            }
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > parent.x && mouseX < parent.x + parent.width && mouseY > parent.y + offset && mouseY < parent.y + offset + parent.height;
    }

    // For the eventual "choose multiple at a time" feature
//    public boolean isHovered(double mouseX, double mouseY) {
//        return mouseX > parent.x && mouseX < parent.x + parent.width && mouseY > parent.y + offset && mouseY < parent.y + offset + parent.height;
//    }

}
