package io.github.lefraudeur.gui.clickgui;

import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.visual.GUI;
import io.github.lefraudeur.utils.renfering.ColorHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    public final static ClickGUI INSTANCE = new ClickGUI();

    private List<Frame> frames;

    public List<Frame> getFrames() {
        return frames;
    }

    private ClickGUI() {
        super(Text.literal("Click Gui"));
        frames = new ArrayList<>();
        int offset = 20;
        for (Category category : Category.values()) {
           frames.add(new Frame(category, offset, 20, 100, 15));
            offset += 120;
        }
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        // Colors from chatgpt? very epic lmao

        // Removed gradient, sorry lagoon
        Color color1 = ColorHelper.fade(new Color(102, 51, 153), 2000, 160); // Surface0
        Color color2 = ColorHelper.fade(new Color(25, 25, 112), 2000, 160); // Surface2
        if(GUI.background.get())
            matrices.fillGradient(0, 0, this.width, this.height, color1.getRGB(), color2.getRGB());

        //super.render(matrices, mouseX, mouseY, delta);
        for (Frame frame : frames) {
            if (!GUI.crystal.get() && frame.category.getName().equals("Crystal")) continue;
            if (!GUI.combatCategory.get() && frame.category.getName().equals("Combat")) continue;
            if (!GUI.move.get() && frame.category.getName().equals("Movement")) continue;
            if (!GUI.exploit.get() && frame.category.getName().equals("Exploit")) continue;
            if (!GUI.playerCategory.get() && frame.category.getName().equals("Player")) continue;
            if (!GUI.misc.get() && frame.category.getName().equals("Misc")) continue;
            
            frame.render(matrices, mouseX, mouseY, delta);
            frame.updatePosition(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            frame.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            frame.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Frame frame : frames) {
            frame.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
