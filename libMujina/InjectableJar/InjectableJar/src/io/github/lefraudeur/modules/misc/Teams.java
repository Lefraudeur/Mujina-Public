package io.github.lefraudeur.modules.misc;

import io.github.lefraudeur.events.PacketReceiveEvent;
import io.github.lefraudeur.gui.settings.types.ModeSetting;
import io.github.lefraudeur.modules.Category;
import io.github.lefraudeur.modules.Info;
import io.github.lefraudeur.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.Objects;

@Info(category = Category.MISC, name = "Teams",  description = "Don't hit my teammates :(", key = Module.key_none)
public final class Teams extends Module {

    private static final ModeSetting mode = new ModeSetting("Mode", "Mode", "Normal", "Armor", "DisplayName");
    // private static final ModeSetting serverMode = new ModeSetting("Server", "The server the thing is using", "Cubecraft", "Minemalia", "Herobrine");

    public static boolean teams = false;

    // public BooleanSetting bool = new BooleanSetting("Boolean", "AA", false);

    @Override
    protected void onEnable() {
        teams = true;
    }

    @Override
    protected void onDisable() {
        teams = false;
    }

    public static boolean isTeam(final Entity entity) {
        if (!teams || !(entity instanceof PlayerEntity e)) return false;

        // check if entity is
        if (mode.isMode("Normal")) {
            if (entity.isTeammate(mc.player)) return false;
            // if that fails, get entity color and your own color, and compare them
            int enemyColor = entity.getTeamColorValue();
            int ownColor = mc.player.getTeamColorValue();
            if (enemyColor == ownColor) return true;
        }

        // armor teams
        // cancer code... my linter is crying
        if (mode.isMode("Armor")) {
            if (!e.getInventory().getArmorStack(2).isEmpty() && !mc.player.getInventory().getArmorStack(2).isEmpty()) {
                if (e.getInventory().getArmorStack(2).getNbt() != null && mc.player.getInventory().getArmorStack(2).getNbt() != null) {
                    if (Objects.requireNonNull(e.getInventory().getArmorStack(2).getNbt()).getCompound("display").getInt("color") == Objects.requireNonNull(mc.player.getInventory().getArmorStack(2).getNbt()).getCompound("display").getInt("color")) {
                        return false;
                    }
                }
            }
        }

        // DisplayName noteams, courtesy of rk3_

        if (mode.isMode("DisplayName")) {

            // getting the player display name
            String color1 = mc.player.getDisplayName().toString();

            // getting the entity display name
            String color2 = entity.getDisplayName().toString();

            // getting the first 3 characters of the player name color
            String playerNameChars = color1.substring(13, 22);

            // getting the first 3 characters of the entity name color
            String entityNameColor = color2.substring(13, 22);

            return playerNameChars.equals(entityNameColor);
        }

        return false;
    }

    @Override
    public void onPacketReceiveEvent(final PacketReceiveEvent event) {

        if (isNull() || mc.getCurrentServerEntry() == null || mc.isInSingleplayer()) return;

        if (!(event.getPacket() instanceof GameMessageS2CPacket packet)) return;

        final String address = mc.getCurrentServerEntry().address.toLowerCase();

        // TODO: make server specific stuff work




    }

   public enum teamState {
        Off,
        On
   }

}
