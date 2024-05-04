package io.github.lefraudeur.gui.clickgui.widgets;

import io.github.lefraudeur.gui.settings.SettingBase;
import net.minecraft.client.util.math.MatrixStack;

public class Component {

    public SettingBase setting;
    public ModuleButton parent;
    public int offset;

    public Component(SettingBase setting, ModuleButton parent, int offset) {
        this.parent = parent;
        this.setting = setting;
        this.offset = offset;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > parent.parent.x && mouseX < parent.parent.x + parent.parent.width && mouseY > parent.parent.y + parent.offset + offset && mouseY < parent.parent.y + parent.offset + parent.parent.height + offset && this.setting.isVisible();
    }

}
