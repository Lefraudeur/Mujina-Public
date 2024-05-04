package io.github.lefraudeur.utils.player;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import static io.github.lefraudeur.Main.mc;

public final class ChatUtils {

    final static Text prefix = Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(Text.literal("Mujina | ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xffc203fc))));

    public static void addChatMessage(final String msg) {
        MutableText message = Text.empty();
        message.append(prefix);
        message.append(msg);
        mc.inGameHud.getChatHud().addMessage(message);
    }

    public static void sendChatMessage(final String message, boolean history) {
        if (mc.getNetworkHandler() == null) return;
        if (history) mc.inGameHud.getChatHud().addToMessageHistory(message);
        if (message.startsWith("/")) mc.getNetworkHandler().sendChatMessage(message);
    }

}


