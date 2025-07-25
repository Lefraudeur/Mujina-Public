#pragma once
#include <string_view>

namespace Mappings
{
	inline constexpr std::string_view net_minecraft_client_MinecraftClient = "net/minecraft/client/Minecraft";
	inline constexpr std::string_view net_minecraft_client_MinecraftClient_instance = "instance";
	inline constexpr std::string_view net_minecraft_client_MinecraftClient_window = "window";


	inline constexpr std::string_view net_minecraft_client_util_Window = "com/mojang/blaze3d/platform/Window";
	inline constexpr std::string_view net_minecraft_client_util_Window_handle = "window";

	inline constexpr std::string_view net_minecraft_client_network_ClientCommonNetworkHandler = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl";

	inline constexpr std::string_view net_minecraft_entity_player_PlayerEntity = "net/minecraft/world/entity/player/Player";
	inline constexpr std::string_view net_minecraft_network_ClientConnection = "net/minecraft/network/Connection";
	inline constexpr std::string_view net_minecraft_client_gui_hud_InGameHud = "net/minecraft/client/gui/Gui";
	inline constexpr std::string_view net_minecraft_client_render_GameRenderer = "net/minecraft/client/renderer/GameRenderer";
	inline constexpr std::string_view net_minecraft_world_BlockCollisionSpliterator = "net/minecraft/world/level/BlockCollisions";
	inline constexpr std::string_view net_minecraft_client_render_WorldRenderer = "net/minecraft/client/renderer/LevelRenderer";
}