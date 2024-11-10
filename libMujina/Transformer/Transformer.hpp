#pragma once
#include <string_view>
#include <array>
#include "../Java/Java.hpp"
#include "../mappings.hpp"

class Transformer : public Base
{
public:
	Transformer(JNI& jni, Java::ClassLoader& minecraftClassLoader);
	~Transformer();

	bool retransform();
private:
	JNI& jni;
	Java::ClassLoader& minecraftClassLoader;
	inline static bool disable = false;

	struct TransformData
	{
		std::string_view target_class;
		std::string_view patcher_name;
	};
	inline static constexpr TransformData to_transform[] =
	{
		{Mappings::net_minecraft_client_MinecraftClient, "patch_net_minecraft_client_MinecraftClient"},
		{Mappings::net_minecraft_client_network_ClientCommonNetworkHandler, "patch_net_minecraft_client_network_ClientCommonNetworkHandler"},
		{Mappings::net_minecraft_entity_player_PlayerEntity, "patch_net_minecraft_entity_player_PlayerEntity"},
		{Mappings::net_minecraft_network_ClientConnection, "patch_net_minecraft_network_ClientConnection"},
		{Mappings::net_minecraft_client_gui_hud_InGameHud, "patch_net_minecraft_client_gui_hud_InGameHud"},
		{Mappings::net_minecraft_client_render_GameRenderer, "patch_net_minecraft_client_render_GameRenderer"},
		{Mappings::net_minecraft_world_BlockCollisionSpliterator, "patch_net_minecraft_world_BlockCollisionSpliterator"},
		{Mappings::net_minecraft_client_render_WorldRenderer, "patch_net_minecraft_client_render_WorldRenderer"}
	};

	static void JNICALL ClassFileLoadHook_callback(jvmtiEnv* jvmti_env,
		JNIEnv* jni_env,
		jclass class_being_redefined,
		jobject loader,
		const char* name,
		jobject protection_domain,
		jint class_data_len,
		const unsigned char* class_data,
		jint* new_class_data_len,
		unsigned char** new_class_data);
};