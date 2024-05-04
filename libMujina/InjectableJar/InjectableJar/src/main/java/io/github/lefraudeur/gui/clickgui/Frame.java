package io.github.lefraudeur.gui.clickgui;

import io.github.lefraudeur.gui.clickgui.widgets.Component;
import io.github.lefraudeur.gui.clickgui.widgets.ModuleButton;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.renfering.ColorHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Colors;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static io.github.lefraudeur.Main.mc;

public class Frame {

    public int x, y, width, height, dragX, dragY;
    public Category category;

    public boolean dragging, extended;
    private final Matrix4f emptyMatrix = new Matrix4f();
    private List<ModuleButton> buttons;


    public Frame(Category category, int x, int y, int width, int height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dragging = false;
        this.extended = false;

        buttons = new ArrayList<>();

        int offset = height;
        for (Module module : category.getModules()) {
            buttons.add(new ModuleButton(module,this, offset));
            offset += height;
        }
    }

    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        // Give the method the shit it needs to render shit
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumerProvider vertexConsumerProvider = mc.getBufferBuilders().getEntityVertexConsumers();
        DrawContext drawContext = new DrawContext(mc, immediate); // Instantiate a concrete implementation

        // draw the actual category button or whatever
        drawContext.fill(x, y, x + width, y + height, ColorHelper.getCategoryRGB(this.category));
        // render category name
        mc.textRenderer.draw(category.getName(), x + 2, y + 2, Colors.WHITE, false, matrices.getMatrices().peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        mc.textRenderer.draw(extended ? "⌄" : "^", x + width - 3 - mc.textRenderer.getWidth("-"), y + 2, Colors.WHITE, false, matrices.getMatrices().peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

        if (extended) {
            // if category is extended, draw module buttons
            for (ModuleButton mb : buttons) {
                mb.render(matrices.getMatrices(), mouseX, mouseY, delta);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                dragging = true;
                dragX = (int) mouseX - x;
                dragY = (int) mouseY - y;
            } else if (button == 1) {

                extended = !extended;

            }
        }
        if (extended) {
            for (ModuleButton buttons : buttons) {
                buttons.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) dragging = false;

        for (ModuleButton mb : buttons) {
            mb.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleButton mb : buttons) {
            mb.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    // for the eventual "choose multiple at a time" feature
//    public boolean isHovered(double mouseX, double mouseY) {
//        return mouseX > x && mouseY < y + width && mouseY > y && mouseY < y + height;
//    }

    public void updateButtons() {
        int offset = height;

        for (ModuleButton button : buttons) {
            button.offset = offset;
            offset += height;

            if (button.extended) {
                for (Component component : button.components) {
                    if (component.setting.isVisible()) offset += height;
                }
            }
        }
    }

    public void updatePosition(double mouseX, double mouseY) {
        if (dragging) {
            x = (int) (mouseX - dragX);
            y = (int) (mouseY - dragY);
        }
    }
}