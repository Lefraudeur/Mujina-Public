package io.github.lefraudeur.modules;

import io.github.lefraudeur.events.*;
import io.github.lefraudeur.gui.settings.SettingBase;
import io.github.lefraudeur.gui.settings.types.BindSetting;
import io.github.lefraudeur.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class Module {

    public static final int key_none = -1481058891;
    public boolean canToggle; //this for keyBind
    private boolean enabled;
    private int keyBind;
    private final Category category;
    private final String name;
    private final String description;

    protected final static MinecraftClient mc = MinecraftClient.getInstance();

    private SettingBase[] settings = new SettingBase[0];
    private final BindSetting bindSetting = new BindSetting(this);

    public Module()
    {
        final Info module = this.getClass().getAnnotation(Info.class);
        category = module.category();
        category.addModule(this);
        keyBind = module.key();
        name = module.name();
        description = module.description();
        enabled = false;
        canToggle = true;
    }

    public void enable()
    {
        if (enabled) return;
        enabled = true;
        onEnable();
    }

    public void disable()
    {
        if (!enabled) return;
        enabled = false;
        onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Module getMe() {
        return this;
    }

    public void toggle() {
        if (enabled) disable();
        else enable();
    }

    protected void onEnable()
    {
    }

    protected void onDisable()
    {
    }
    public void onPacketSendEvent(final PacketSendEvent event)
    {
    }

    public void onPreTickEvent(final PreTickEvent event)
    {
    }

    public void onPostTickEvent(final PostTickEvent event)
    {

    }

    public void onAttackEvent(final AttackEvent event)
    {
    }

    public void onPacketReceiveEvent(final PacketReceiveEvent event)
    {
    }

    public void onPreRender2DEvent(PreRender2DEvent event)
    {
    }

    public void onPostRender2DEvent(PostRender2DEvent event)
    {
    }

    public void onPreDoAttackEvent(PreDoAttackEvent event)
    {
    }

    public void onPostDoAttackEvent(PostDoAttackEvent event)
    {
    }

    public void onMidUpdateTargetedEntityEvent(MidUpdateTargetedEntityEvent event)
    {
    }
    public void onBlockCollisionEvent(BlockCollisionEvent event)
    {
    }

    public boolean isNull()
    {
        return (mc.player == null || mc.world == null);
    }

    public void send(final Packet<?> packetIn) //useless + wrong place
     {
        if (packetIn == null) {
            return;
        }
        Objects.requireNonNull(mc.getNetworkHandler()).getConnection().send(packetIn);
    }

    public void message(String message) {
        ChatUtils.addChatMessage(message);
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKey(final int bind) {
        this.keyBind = bind;
    }

    public int getDefaultKey()
    {
        return keyBind;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public SettingBase[] getSettings() {
        return settings;
    }

    public void addSetting(final SettingBase setting) {
        SettingBase[] newSettingsArray = new SettingBase[settings.length + 1];
        System.arraycopy(settings, 0, newSettingsArray, 0, settings.length);
        newSettingsArray[settings.length] = setting;
        settings = newSettingsArray;
    }

    public void registerSettings() {
        settings = Stream.of(this.getClass().getDeclaredFields(), Module.class.getDeclaredFields()).flatMap(Stream::of)
                .filter(field -> SettingBase.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return (SettingBase)field.get(this);
                    } catch (IllegalAccessException ignore) {
                        return null;
                    }
                }).toArray(SettingBase[]::new);
    }
}
