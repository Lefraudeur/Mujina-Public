package io.github.lefraudeur.utils.player;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.*;

import static io.github.lefraudeur.Main.mc;

public class SlotUtils
{
    public static final int HOTBAR_START = 0;
    public static final int HOTBAR_END = 8;

    public static final int OFFHAND = 45;

    public static final int MAIN_START = 9;
    public static final int MAIN_END = 35;

    public static final int ARMOR_START = 36;
    public static final int ARMOR_END = 39;

    public static int indexToId(int i) {
        if (mc.player == null) return -1;
        ScreenHandler handler = mc.player.currentScreenHandler;

        if (handler instanceof PlayerScreenHandler) return survivalInventory(i);
        if (handler instanceof CreativeInventoryScreen.CreativeScreenHandler) return creativeInventory(i);
        if (handler instanceof GenericContainerScreenHandler genericContainerScreenHandler) return genericContainer(i, genericContainerScreenHandler.getRows());
        if (handler instanceof CraftingScreenHandler) return craftingTable(i);
        if (handler instanceof FurnaceScreenHandler) return furnace(i);
        if (handler instanceof BlastFurnaceScreenHandler) return furnace(i);
        if (handler instanceof SmokerScreenHandler) return furnace(i);
        if (handler instanceof Generic3x3ContainerScreenHandler) return generic3x3(i);
        if (handler instanceof EnchantmentScreenHandler) return enchantmentTable(i);
        if (handler instanceof BrewingStandScreenHandler) return brewingStand(i);
        if (handler instanceof MerchantScreenHandler) return villager(i);
        if (handler instanceof BeaconScreenHandler) return beacon(i);
        if (handler instanceof AnvilScreenHandler) return anvil(i);
        if (handler instanceof HopperScreenHandler) return hopper(i);
        if (handler instanceof ShulkerBoxScreenHandler) return genericContainer(i, 3);
        if (handler instanceof CartographyTableScreenHandler) return cartographyTable(i);
        if (handler instanceof GrindstoneScreenHandler) return grindstone(i);
        if (handler instanceof LecternScreenHandler) return lectern();
        if (handler instanceof LoomScreenHandler) return loom(i);
        if (handler instanceof StonecutterScreenHandler) return stonecutter(i);

        return -1;
    }

    private static int survivalInventory(int i) {
        if (isHotbar(i)) return 36 + i;
        if (isArmor(i)) return 5 + (i - 36);
        return i;
    }

    private static int creativeInventory(int i) {
        if (!(mc.currentScreen instanceof CreativeInventoryScreen))
            return -1;
        return survivalInventory(i);
    }

    private static int genericContainer(int i, int rows) {
        if (isHotbar(i)) return (rows + 3) * 9 + i;
        if (isMain(i)) return rows * 9 + (i - 9);
        return -1;
    }

    private static int craftingTable(int i) {
        if (isHotbar(i)) return 37 + i;
        if (isMain(i)) return i + 1;
        return -1;
    }

    private static int furnace(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isMain(i)) return 3 + (i - 9);
        return -1;
    }

    private static int generic3x3(int i) {
        if (isHotbar(i)) return 36 + i;
        if (isMain(i)) return i;
        return -1;
    }

    private static int enchantmentTable(int i) {
        if (isHotbar(i)) return 29 + i;
        if (isMain(i)) return 2 + (i - 9);
        return -1;
    }

    private static int brewingStand(int i) {
        if (isHotbar(i)) return 32 + i;
        if (isMain(i)) return 5 + (i - 9);
        return -1;
    }

    private static int villager(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isMain(i)) return 3 + (i - 9);
        return -1;
    }

    private static int beacon(int i) {
        if (isHotbar(i)) return 28 + i;
        if (isMain(i)) return 1 + (i - 9);
        return -1;
    }

    private static int anvil(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isMain(i)) return 3 + (i - 9);
        return -1;
    }

    private static int hopper(int i) {
        if (isHotbar(i)) return 32 + i;
        if (isMain(i)) return 5 + (i - 9);
        return -1;
    }



    private static int cartographyTable(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isMain(i)) return 3 + (i - 9);
        return -1;
    }

    private static int grindstone(int i) {
        if (isHotbar(i)) return 30 + i;
        if (isMain(i)) return 3 + (i - 9);
        return -1;
    }

    private static int lectern() {
        return -1;
    }

    private static int loom(int i) {
        if (isHotbar(i)) return 31 + i;
        if (isMain(i)) return 4 + (i - 9);
        return -1;
    }

    private static int stonecutter(int i) {
        if (isHotbar(i)) return 29 + i;
        if (isMain(i)) return 2 + (i - 9);
        return -1;
    }

    // Utils

    public static boolean isHotbar(int i) {
        return i >= HOTBAR_START && i <= HOTBAR_END;
    }

    public static boolean isMain(int i) {
        return i >= MAIN_START && i <= MAIN_END;
    }

    public static boolean isArmor(int i) {
        return i >= ARMOR_START && i <= ARMOR_END;
    }
}

