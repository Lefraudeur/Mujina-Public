package io.github.lefraudeur.utils;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.lefraudeur.Main.mc;

public class RobotUtil {

        static Robot robot;

        static {
            try {
                robot = new Robot();
            } catch (AWTException e) {
            }
        }

        public static void simulateLeftClick() throws AWTException {
            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.submit(() -> {

                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

                robot.delay(100);

                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            });
            executor.shutdown();
        }

        public static void simulateRightClick() throws AWTException {
            ExecutorService executor = Executors.newFixedThreadPool(2);

            executor.submit(() -> {
                robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);

                robot.delay(100);

                robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            });
            executor.shutdown();
        }

        public static boolean isRightClickBound(KeyBinding key) {
            return key.matchesMouse(org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        }

        public static boolean isLeftClickBound(KeyBinding key) {
            return key.matchesMouse(org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT);
        }

    public static boolean canAttack() {

        if (mc.isPaused()) return false;
        if (mc.currentScreen != null) return false;
        if (mc.player.isSpectator()) return false;
        if (mc.player.isSleeping()) return false;
        if (mc.player.isDead()) return false;

        return true;
    }

    public static boolean canInteractWithItem() {

        if (mc.isPaused()) return false;
        if (mc.currentScreen != null) return false;
        if (mc.player.isSpectator()) return false;
        if (mc.player.isSleeping()) return false;
        if (mc.player.isDead()) return false;

        ItemStack itemStack = mc.player.getMainHandStack();

        if (mc.player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return false;

        return true;
    }

    public static void simulateInteract() {
        try {
            if (canInteractWithItem()) {
                if (isRightClickBound(mc.options.useKey)) {
                    simulateRightClick();
                } else if (isLeftClickBound(mc.options.useKey)) {
                    simulateLeftClick();
                }
            }
        } catch (Exception e) {
        }
    }

    public static void simulateAttack() {
        try {
            if (canAttack()) {
                if (isRightClickBound(mc.options.attackKey)) {
                    simulateRightClick();
                } else if (isLeftClickBound(mc.options.attackKey)) {
                    simulateLeftClick();
                }
            }
        } catch (Exception e) {
        }
    }

}
