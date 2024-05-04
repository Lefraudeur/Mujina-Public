package io.github.lefraudeur.modules.visual;

import io.github.lefraudeur.Main;
import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.events.PreRender2DEvent;
import io.github.lefraudeur.gui.settings.types.BooleanSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import io.github.lefraudeur.utils.MathUtils;
import io.github.lefraudeur.utils.ReflectionHelper;
import io.github.lefraudeur.utils.player.PlayerUtils;
import io.github.lefraudeur.utils.renfering.ColorHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@Info(category = Category.VISUAL, name = "HUD",  description = "Opens a GUI to config the cheat", key = Module.key_none)
public final class HUD extends Module {
    public HUD() {
        super();
    }

    private long lastPacket = 0;

    public BooleanSetting japanese = new BooleanSetting("Japanese", "Japanese HUD", false);
    public BooleanSetting coords = new BooleanSetting("Coordinates", "draw the Coordinates", false);
    public BooleanSetting outlineLogo = new BooleanSetting("logo outline", "draw the Coordinates", false);
    public BooleanSetting fps = new BooleanSetting("fps", "draw the Coordinates", false);
    public BooleanSetting ping = new BooleanSetting("ping", "draw the Coordinates", false);
    public BooleanSetting timeStamp = new BooleanSetting("timestamp", "draw the Coordinates", false);
    public BooleanSetting runSpeed = new BooleanSetting("runspeed", "draw the Coordinates", false);
    public BooleanSetting arrayList = new BooleanSetting("arrayList", "ArrayList", false);

    private double speedMeter = 0;

    public void render(MatrixStack matrices, float tickDelta) {

    }


    @Override
    public void onPreRender2DEvent(final PreRender2DEvent event) {

        // if (getModuleByClass(GUI.class).isEnabled()) return;

        drawLagMeter(event.getDrawContext().getMatrices());
        if (arrayList.get())
            drawArrayList(event.getDrawContext());
        waterMark(event.getDrawContext());
    }

    @Override
    public void onPacketReceiveEvent(final PacketReceiveEvent event) {
        if(isNull() || mc.options.getReducedDebugInfo().getValue() || mc.currentScreen instanceof DeathScreen) return;
        lastPacket = System.currentTimeMillis();
    }

    public void drawLagMeter(MatrixStack matrices) {
        final long current = System.currentTimeMillis();

        VertexConsumerProvider vertexConsumerProvider = mc.getBufferBuilders().getEntityVertexConsumers();
        if (current - lastPacket > 500) {
            final Window sr = mc.getWindow();
            final String text = "Server Lagging For: " + String.format("%.2f", (current - lastPacket) / 1000d) + "s";
            final int xd = sr.getScaledWidth() + 72 - mc.textRenderer.getWidth(text) / 2;

            mc.textRenderer.draw(text, xd, sr.getScaledWidth(), Colors.WHITE, true, matrices.peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            //mc.textRenderer.draw(Text.literal(text), xd, sr.getScaledHeight() + 1, MathHelper.clamp((int) (current - lastPacket - 500 / 3, 5, 255) << 24 | 0xd0d0d0); //drawWithShadow(stack, text, xd, sr.getScaledHeight() + 1, MathHelper.clamp((int) (current - lastPacket - 500) / 3, 5, 255) << 24 | 0xd0d0d0);
        }
    }

    private void drawArrayList(DrawContext context) {
        final int backgroundColor = 0x995A5A5A; // ARGB
        final int fontHeight = mc.textRenderer.fontHeight - 1;

        List<String> moduleNames = Arrays.asList(Arrays.stream(Main.getEnabledSortedModules()).map(Module::getName).toArray(String[]::new));

        if (!moduleNames.isEmpty()) {
            int xPosition = mc.getWindow().getScaledWidth() - 2;
            int yOffset = 2;
            int latestModuleWidth = 0;

            for (String moduleName : moduleNames) {
                if (moduleName == null) continue;

                int moduleHeight = yOffset * (fontHeight + 3);
                int moduleWidth = mc.textRenderer.getWidth(moduleName) + 7;

                if (latestModuleWidth == 0) latestModuleWidth = moduleWidth;

                int moduleX = xPosition - moduleWidth;
                int moduleY = moduleHeight;

                fill(context, moduleX, moduleY, xPosition, moduleY + 5, backgroundColor);
                drawText(moduleName, moduleX + 3, moduleY + 1, -1, context.getMatrices());

                yOffset++;
            }
        }
    }


    private List<String> waterMarkAndInfo() {
        final List<String> infos = new ArrayList<>();

            if(timeStamp.get()) infos.add("Time: " + Formatting.YELLOW + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd HH:mm:ss zzz")));


        if (runSpeed.get()) {
            final double x = mc.player.getX() - mc.player.prevX;
            final double z = mc.player.getZ() - mc.player.prevZ;

            speedMeter = MathUtils.round(Math.hypot(x, z) / 1000.0f / (0.05f / 3600.0f), 2);

            final String speed = Formatting.WHITE + "Speed " + Formatting.GREEN + speedMeter + " km/h" + Formatting.RESET;
            infos.add(speed);
        }

        if (ping.get() && !mc.isInSingleplayer()) {
            final int pingValue = PlayerUtils.getPing(mc.player);
            final Formatting color = ColorHelper.getLatencyColor(pingValue);

            final String lat = Formatting.WHITE + "Ping " + color + pingValue + Formatting.RESET;
            infos.add(lat);
        }

        if (fps.get()) {
            final int frames = (int) ReflectionHelper.getFieldValue(mc, "field_1738", "currentFps");
            final String fps = Formatting.WHITE + "FPS " + Formatting.DARK_GREEN + frames + Formatting.RESET;
            infos.add(fps);
        }

        infos.sort(Comparator.comparingDouble(e -> -mc.textRenderer.getWidth(e)));

        return infos;
    }

    private void waterMark(DrawContext stack) {
        final int offset = 7;
        final int rainBow = ColorHelper.rainBowSimple(), backGround = 0x995A5A5A; // ARGB
        final int fontHeight = mc.textRenderer.fontHeight - 1;

        String clientName = (japanese.get() ? "ムジナ" : "Mujina");
        final int widthLogo = mc.textRenderer.getWidth(clientName + " " + Main.clientVersion) + offset + 5;
        // Right outline of logo.
        if (outlineLogo.get()) fill(stack, widthLogo, 1, widthLogo + 1, fontHeight + 4, rainBow);

        /* Black background. */
        fill(stack, 2, 2, widthLogo, fontHeight + 5, backGround);


        drawText(clientName, 5, 4, 0xFF6494FF, stack.getMatrices()); // You can change the color code as needed
        drawText(Formatting.BLUE + "v" + Main.clientVersion, mc.textRenderer.getWidth(clientName) + 8, 4, -1, stack.getMatrices());

        int amount = 0;
        int firstIndex = 2;
        int latestIndex = 0;

        List<String> infos = new ArrayList<>();
        List<String> arrayListItems = new ArrayList<>();
        infos = waterMarkAndInfo();

        int screenWidth = mc.getWindow().getScaledWidth();
        int xPosition = screenWidth - 2;



        if (!infos.isEmpty()) {
            for (int i = 0; i < infos.size(); ++i) {
                final String info = infos.get(i);

                if (info == null) continue;

                final int height = 13 + amount * (fontHeight + 3);
                final int width = mc.textRenderer.getWidth(info) + 7;

                if (i == infos.size() - 1) latestIndex = width;
                else if (i == 0) firstIndex = width;

                fill(stack, 2, height, width, height + 11, backGround);
                drawText(info, 5, height + 1, -1, stack.getMatrices());

                if (outlineLogo.get()) {
                    // Right outline of infos.
                    fill(stack, width, height, width + 1, height + 11, rainBow);

                    // Bottom outline of infos.
                    if (i != infos.size() - 1)
                        fill(stack, width - (width - mc.textRenderer.getWidth(infos.get(i + 1)) - 8), height + fontHeight + 3, width + 1, height + fontHeight + 4, rainBow);
                }
                amount++;
            }
        }

        if (outlineLogo.get()) {
            /* Outline rainbow. */

            // Line under logo.
            fill(stack, firstIndex, fontHeight + 4, widthLogo + 1, fontHeight + 5, rainBow);

            // Up
            fill(stack, 2, 1, widthLogo, 2, rainBow);

            final int infoHeight = 13 + amount * (fontHeight + 3);

            // Left
            fill(stack, 2, 1, 3, infoHeight, rainBow);

            // Down
            fill(stack, 2, infoHeight, latestIndex + 1, infoHeight + 1, rainBow);
        }

        if (coords.get()) {
            if (isNull()) return;
            final boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
            final BlockPos pos = mc.player.getBlockPos();
            final Vec3d vec = mc.player.getPos();
            final BlockPos pos2 = nether ? new BlockPos((int) (vec.getX() * 8), (int) vec.getY(), (int) (vec.getZ() * 8)) : new BlockPos((int) (vec.getX() / 8), (int) vec.getY(), (int) (vec.getZ() / 8));
            final String coordsStr = "XYZ: " + (nether ? Formatting.RED : Formatting.GREEN) + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " \u00a77[" + (nether ? "\u00a7b" : "\u00a74") + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ() + "\u00a77]";
            final int height = mc.getWindow().getScaledHeight() - (mc.currentScreen instanceof ChatScreen ? 28 : 15);

            // Background
            fill(stack, 2, height, mc.textRenderer.getWidth(coordsStr) + 6, height + mc.textRenderer.fontHeight + 2, backGround);

            // Outlines
            fill(stack, 2, height, mc.textRenderer.getWidth(coordsStr) + 7, height + 1, rainBow);
            fill(stack, 2, height, 3, height + mc.textRenderer.fontHeight + 2, rainBow);
            fill(stack, 2, height + mc.textRenderer.fontHeight + 2, mc.textRenderer.getWidth(coordsStr) + 7, height + mc.textRenderer.fontHeight + 3, rainBow);
            fill(stack, mc.textRenderer.getWidth(coordsStr) + 6, height, mc.textRenderer.getWidth(coordsStr) + 7, height + mc.textRenderer.fontHeight + 3, rainBow);

            drawText(coordsStr, 5, height + 2, -1, stack.getMatrices());
        }
    }


    private void drawText(final String text, final int x, final int y, final int color, MatrixStack matrices) {
        VertexConsumerProvider vertexConsumerProvider = mc.getBufferBuilders().getEntityVertexConsumers();
        mc.textRenderer.draw(text, x, y, color, true, matrices.peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    }

    private void fill(DrawContext context, int x, int y, int x2, int y2, int color) {
        context.fill(x, y, x2, y2, color);
    }
}
