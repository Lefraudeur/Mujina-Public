package io.github.lefraudeur;

import org.objectweb.asm.*;

import java.util.function.Consumer;

public final class Patcher  {
    public static final int ASM_VERSION = Opcodes.ASM9;
    //TODO: auto remap
    public static final String obf_net_minecraft_client_network_ClientCommonNetworkHandler_sendPacket = "send";
    public static final String obf_net_minecraft_network_packet_Packet = "net/minecraft/network/protocol/Packet";
    public static final String obf_net_minecraft_client_network_ClientCommonNetworkHandler = "net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl";
    public static final String obf_net_minecraft_client_MinecraftClient = "net/minecraft/client/Minecraft";
    public static final String obf_net_minecraft_client_MinecraftClient_tick = "tick";
    public static final String obf_net_minecraft_client_MinecraftClient_doAttack = "startAttack";

    public static final String obf_net_minecraft_entity_player_PlayerEntity = "net/minecraft/world/entity/player/Player";
    public static final String obf_net_minecraft_entity_player_PlayerEntity_attack = "attack";
    public static final String obf_net_minecraft_entity_Entity = "net/minecraft/world/entity/Entity";

    public static final String obf_net_minecraft_network_ClientConnection = "net/minecraft/network/Connection";
    public static final String obf_net_minecraft_network_ClientConnection_channelRead0 = "channelRead0";
    public static final String io_netty_channel_ChannelHandlerContext = "io/netty/channel/ChannelHandlerContext";

    public static final String obf_net_minecraft_client_gui_hud_InGameHud = "net/minecraft/client/gui/Gui";
    public static final String obf_net_minecraft_client_gui_hud_InGameHud_render = "render";
    public static final String obf_net_minecraft_client_gui_DrawContext = "net/minecraft/client/gui/GuiGraphics";

    public static final String obf_net_minecraft_client_render_GameRenderer = "net/minecraft/client/renderer/GameRenderer";
    public static final String obf_net_minecraft_client_render_GameRenderer_updateTargetedEntity = "pick";

    public static final String obf_net_minecraft_block_AbstractBlock$AbstractBlockState = "net/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase";
    public static final String obf_net_minecraft_block_AbstractBlock$AbstractBlockState_getCollisionShape = "getCollisionShape";
    public static final String obf_net_minecraft_world_BlockView = "net/minecraft/world/level/BlockGetter";
    public static final String obf_net_minecraft_util_math_BlockPos = "net/minecraft/core/BlockPos";
    public static final String obf_net_minecraft_block_ShapeContext = "net/minecraft/world/phys/shapes/CollisionContext";
    public static final String obf_net_minecraft_block_BlockState = "net/minecraft/world/level/block/state/BlockState";
    public static final String obf_net_minecraft_util_shape_VoxelShape = "net/minecraft/world/phys/shapes/VoxelShape";

    public static final String obf_net_minecraft_client_render_WorldRenderer = "net/minecraft/client/renderer/LevelRenderer";
    public static final String obf_net_minecraft_client_render_WorldRenderer_render = "renderLevel";
    public static final String obf_net_minecraft_client_util_math_MatrixStack = "com/mojang/blaze3d/vertex/PoseStack";
    public static final String obf_net_minecraft_client_render_Camera = "net/minecraft/client/Camera";
    public static final String obf_net_minecraft_client_render_LightmapTextureManager = "net/minecraft/client/renderer/LightTexture";

    public static final String org_joml_Matrix4f = "org/joml/Matrix4f";


    //TODO: reduce repeating code
    public static byte[] patch_net_minecraft_client_network_ClientCommonNetworkHandler(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
                obf_net_minecraft_client_network_ClientCommonNetworkHandler_sendPacket,
                "(L" + obf_net_minecraft_network_packet_Packet + ";)V",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PacketSendEvent", obf_net_minecraft_client_network_ClientCommonNetworkHandler, obf_net_minecraft_network_packet_Packet);
                    check_cancel_void(mv, index);
                },
                null,
                null
        );
        MethodModifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_client_MinecraftClient(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
                obf_net_minecraft_client_MinecraftClient_tick,
                "()V",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PreTickEvent", obf_net_minecraft_client_MinecraftClient);
                    check_cancel_void(mv, index);
                },
                (MethodVisitor mv) ->
                {
                    store_and_post_event(mv, "io.github.lefraudeur.events.PostTickEvent", obf_net_minecraft_client_MinecraftClient);
                },
                null
        );
        MethodModifier mod2 = new MethodModifier
        (
                obf_net_minecraft_client_MinecraftClient_doAttack,
                "()Z",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PreDoAttackEvent", obf_net_minecraft_client_MinecraftClient);
                    check_cancel_boolean(mv, index);
                },
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PostDoAttackEvent", obf_net_minecraft_client_MinecraftClient);
                    check_cancel_boolean(mv, index);
                },
                null
        );
        MethodModifier[] modifiers = {mod, mod2};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_entity_player_PlayerEntity(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
                obf_net_minecraft_entity_player_PlayerEntity_attack,
                "(L" + obf_net_minecraft_entity_Entity +";)V",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.AttackEvent", obf_net_minecraft_entity_player_PlayerEntity, obf_net_minecraft_entity_Entity);
                    check_cancel_void(mv, index);
                },
                null,
                null
        );
        MethodModifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_network_ClientConnection(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
                obf_net_minecraft_network_ClientConnection_channelRead0,
                "(L" + io_netty_channel_ChannelHandlerContext + ";L" + obf_net_minecraft_network_packet_Packet + ";)V",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PacketReceiveEvent", obf_net_minecraft_network_ClientConnection, io_netty_channel_ChannelHandlerContext, obf_net_minecraft_network_packet_Packet);
                    check_cancel_void(mv, index);
                },
                null,
                null
        );
        MethodModifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_client_gui_hud_InGameHud(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
                obf_net_minecraft_client_gui_hud_InGameHud_render,
                "(L" + obf_net_minecraft_client_gui_DrawContext + ";F)V",
                (MethodVisitor mv) ->
                {
                    int index = store_and_post_event(mv, "io.github.lefraudeur.events.PreRender2DEvent", obf_net_minecraft_client_gui_hud_InGameHud, obf_net_minecraft_client_gui_DrawContext, "F_primitive");
                    check_cancel_void(mv, index);
                },
                (MethodVisitor mv) ->
                {
                    store_and_post_event(mv, "io.github.lefraudeur.events.PostRender2DEvent", obf_net_minecraft_client_gui_hud_InGameHud, obf_net_minecraft_client_gui_DrawContext, "F_primitive");
                },
                null
        );
        MethodModifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_client_render_GameRenderer(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodConstantModifier mod = new MethodConstantModifier
        (
            obf_net_minecraft_client_render_GameRenderer_updateTargetedEntity,
            "(F)V",
            9.0,
            (MethodVisitor mv)->
            {
                int index = store_and_post_event(mv, "io.github.lefraudeur.events.MidUpdateTargetedEntityEvent", obf_net_minecraft_client_render_GameRenderer, "F_primitive");
                invoke_void_event_method(mv, index, "getNewDoubleValue");
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            }
        );
        Modifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_world_BlockCollisionSpliterator(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        final int freeLocalIndexStart = MethodBasicInfoClassVisitor.getMaxLocals(classReader, "computeNext", "()Ljava/lang/Object;");
        final String targetMethodSig = "(L" + obf_net_minecraft_world_BlockView + ";L" + obf_net_minecraft_util_math_BlockPos + ";L" + obf_net_minecraft_block_ShapeContext + ";)L" + obf_net_minecraft_util_shape_VoxelShape + ";";
        MethodOnInvokeModifier mod = new MethodOnInvokeModifier
        (
            "computeNext",
            "()Ljava/lang/Object;",
            obf_net_minecraft_block_AbstractBlock$AbstractBlockState,
            obf_net_minecraft_block_AbstractBlock$AbstractBlockState_getCollisionShape,
            targetMethodSig,
            (MethodVisitor mv) ->
            {
                int eventIndex = store_and_post_event_args_on_stack(mv, "io.github.lefraudeur.events.BlockCollisionEvent", freeLocalIndexStart, obf_net_minecraft_block_BlockState, obf_net_minecraft_world_BlockView, obf_net_minecraft_util_math_BlockPos, obf_net_minecraft_block_ShapeContext);

                Label continue_exec = new Label();
                doIfCancelled(mv, eventIndex, ()->
                {
                    invoke_void_event_method(mv, eventIndex, "getReturnValue");
                    mv.visitTypeInsn(Opcodes.CHECKCAST, obf_net_minecraft_util_shape_VoxelShape);
                    mv.visitJumpInsn(Opcodes.GOTO, continue_exec);
                });

                invoke_void_event_method(mv, eventIndex, "getBlockState");
                mv.visitTypeInsn(Opcodes.CHECKCAST, obf_net_minecraft_block_BlockState);
                invoke_void_event_method(mv, eventIndex, "getWorld");
                mv.visitTypeInsn(Opcodes.CHECKCAST, obf_net_minecraft_world_BlockView);
                invoke_void_event_method(mv, eventIndex, "getPos");
                mv.visitTypeInsn(Opcodes.CHECKCAST, obf_net_minecraft_util_math_BlockPos);
                invoke_void_event_method(mv, eventIndex, "getContext");
                mv.visitTypeInsn(Opcodes.CHECKCAST, obf_net_minecraft_block_ShapeContext);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, obf_net_minecraft_block_AbstractBlock$AbstractBlockState, obf_net_minecraft_block_AbstractBlock$AbstractBlockState_getCollisionShape, targetMethodSig, false);

                mv.visitLabel(continue_exec);
            }
        );
        Modifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    public static byte[] patch_net_minecraft_client_render_WorldRenderer(byte[] original_class)
    {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassReader classReader = new ClassReader(original_class);
        MethodModifier mod = new MethodModifier
        (
            obf_net_minecraft_client_render_WorldRenderer_render,
            "(L" + obf_net_minecraft_client_util_math_MatrixStack + ";FJZL"
            + obf_net_minecraft_client_render_Camera + ";L"
            + obf_net_minecraft_client_render_GameRenderer + ";L"
            + obf_net_minecraft_client_render_LightmapTextureManager + ";L"
            + org_joml_Matrix4f + ";)V",
            (MethodVisitor mv) ->
            {
                int index = store_and_post_event(mv, "io.github.lefraudeur.events.PreRender3DEvent",
                obf_net_minecraft_client_render_WorldRenderer,
                obf_net_minecraft_client_util_math_MatrixStack,
                "F_primitive", "J_primitive", "Z_primitive",
                obf_net_minecraft_client_render_Camera,
                obf_net_minecraft_client_render_GameRenderer,
                obf_net_minecraft_client_render_LightmapTextureManager,
                org_joml_Matrix4f);
                check_cancel_void(mv, index);
            },
            null,
            (MethodVisitor mv) ->
            {
                store_and_post_event(mv, "io.github.lefraudeur.events.PostRender3DEvent",
                obf_net_minecraft_client_render_WorldRenderer,
                obf_net_minecraft_client_util_math_MatrixStack,
                "F_primitive", "J_primitive", "Z_primitive",
                obf_net_minecraft_client_render_Camera,
                obf_net_minecraft_client_render_GameRenderer,
                obf_net_minecraft_client_render_LightmapTextureManager,
                org_joml_Matrix4f);
            }
        );
        MethodModifier[] modifiers = {mod};
        MethodClassTransformer methodClassTransformer = new MethodClassTransformer(ASM_VERSION, classWriter, classReader, modifiers);
        classReader.accept(methodClassTransformer, 0);
        return classWriter.toByteArray();
    }

    //IGNORE LMAO

    // I did not ignore, I fixed :uwu:
    private static void push_class(MethodVisitor mv, String name, boolean use_other_cl) {
        if (use_other_cl) {
            mv.visitLdcInsn(name);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/ClassLoader", "getSystemClassLoader", "()Ljava/lang/ClassLoader;", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", false);
            return;
        }

        switch (name) {
            case "I_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;"); return; }
            case "Z_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;"); return;}
            case "J_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;"); return; }
            case "F_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;"); return; }
            case "D_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;"); return;}
            case "S_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;"); return; }
            case "B_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;"); return; }
            case "C_primitive" -> { mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;"); return; }
        }
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
    }


    private static void push_array_of_params(MethodVisitor mv, String... types)
    {
        push_array_of_params(mv, 0, types);
    }

    private static void push_array_of_params(MethodVisitor mv, int startIndex, String... types)
    {
        mv.visitLdcInsn(types.length);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        int param_index = startIndex;
        for (int i = 0; i < types.length; ++i)
        {
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(i);
            switch (types[i])
            {
                case "S_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Short");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.ILOAD, param_index);
                    mv.visitInsn(Opcodes.I2S);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Short", "<init>", "(S)V", false);
                    break;
                case "Z_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Boolean");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.ILOAD, param_index);
                    mv.visitInsn(Opcodes.I2B);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Boolean", "<init>", "(Z)V", false);
                    break;
                case "I_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Integer");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.ILOAD, param_index);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
                    break;
                case "J_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Long");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.LLOAD, param_index);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V", false);
                    ++param_index; // J takes 2 local variable indexes
                    break;
                case "F_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Float");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.FLOAD, param_index);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Float", "<init>", "(F)V", false);
                    break;
                case "D_primitive":
                    mv.visitTypeInsn(Opcodes.NEW, "java/lang/Double");
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitVarInsn(Opcodes.DLOAD, param_index);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V", false);
                    ++param_index; // D takes 2 local variable indexes
                    break;
                default:
                    mv.visitVarInsn(Opcodes.ALOAD, param_index);
            }
            mv.visitInsn(Opcodes.AASTORE);
            ++param_index;
        }
    }

    private static void store_params_in_localvar_table(MethodVisitor mv, int freeLocalIndexStart, String... types)
    {
        for (int i = types.length - 1; i >= 0; i--) //warning
            mv.visitVarInsn(Opcodes.ASTORE, freeLocalIndexStart + i); //TODO: handle primitive types
    }

    private static void push_array_of_classes(MethodVisitor mv, String... class_names)
    {
        mv.visitLdcInsn(class_names.length);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < class_names.length; ++i)
        {
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn(i);
            push_class(mv, class_names[i].replace('/', '.'), false);
            mv.visitInsn(Opcodes.AASTORE);
        }
    }

    private static int store_and_post_event(MethodVisitor mv, String EventClassName, String... ConstructorClassNames) // returns the local var index in which event obj is stored
    {
        int fixed_length = ConstructorClassNames.length;
        // ugly, quick fix because J and D take 2 local variable indexes
        for (String t : ConstructorClassNames)
            if (t.equals("J_primitive") || t.equals("D_primitive")) fixed_length++;
        final int EventClass_index = fixed_length;
        final int EventObject_index = fixed_length + 1;

        push_class(mv, EventClassName.replace('/', '.'), true);
        mv.visitVarInsn(Opcodes.ASTORE, EventClass_index);

        mv.visitVarInsn(Opcodes.ALOAD, EventClass_index);
        push_array_of_classes(mv, ConstructorClassNames);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);

        push_array_of_params(mv, ConstructorClassNames);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitVarInsn(Opcodes.ASTORE, EventObject_index);

        mv.visitVarInsn(Opcodes.ALOAD, EventClass_index);
        mv.visitLdcInsn("dispatch");
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);

        mv.visitVarInsn(Opcodes.ALOAD, EventObject_index);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(Opcodes.POP);

        return EventObject_index;
    }

    private static int store_and_post_event_args_on_stack(MethodVisitor mv, String EventClassName, int freeLocalIndexStart, String... ConstructorClassNames) // returns the local var index in which event obj is stored
    {
        final int EventClass_index = freeLocalIndexStart;
        final int EventObject_index = freeLocalIndexStart + 1;

        store_params_in_localvar_table(mv, freeLocalIndexStart + 2, ConstructorClassNames);

        push_class(mv, EventClassName.replace('/', '.'), true);
        mv.visitVarInsn(Opcodes.ASTORE, EventClass_index);

        mv.visitVarInsn(Opcodes.ALOAD, EventClass_index);
        push_array_of_classes(mv, ConstructorClassNames);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);

        push_array_of_params(mv, freeLocalIndexStart + 2, ConstructorClassNames);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitVarInsn(Opcodes.ASTORE, EventObject_index);

        mv.visitVarInsn(Opcodes.ALOAD, EventClass_index);
        mv.visitLdcInsn("dispatch");
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);

        mv.visitVarInsn(Opcodes.ALOAD, EventObject_index);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(Opcodes.POP);

        return EventObject_index;
    }

    private static void invoke_void_event_method(MethodVisitor mv, int eventIndex, String methodName)
    {
        mv.visitVarInsn(Opcodes.ALOAD, eventIndex - 1);
        mv.visitLdcInsn(methodName);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);

        mv.visitVarInsn(Opcodes.ALOAD, eventIndex);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
    }

    private static void check_cancel_void(MethodVisitor mv, int eventIndex)
    {
        doIfCancelled(mv, eventIndex, ()->mv.visitInsn(Opcodes.RETURN));
    }

    private static void check_cancel_boolean(MethodVisitor mv, int eventIndex)
    {
        doIfCancelled(mv, eventIndex, ()->
        {
            invoke_void_event_method(mv, eventIndex, "getReturnValue");
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);

            mv.visitInsn(Opcodes.IRETURN);
        });
    }

    public static void doIfCancelled(MethodVisitor mv, int eventIndex, Runnable todo)
    {
        mv.visitVarInsn(Opcodes.ALOAD, eventIndex - 1);
        mv.visitLdcInsn("isCancelled");
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);

        mv.visitVarInsn(Opcodes.ALOAD, eventIndex);
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        Label continue_exec = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, continue_exec);

        todo.run();

        mv.visitLabel(continue_exec);
    }

    private static class MethodBasicInfoClassVisitor extends ClassVisitor
    {
        private int returnCount;
        private int maxLocalsValue;
        private final String methodName;
        private final String methodSig;
        private MethodBasicInfoClassVisitor(int api, String methodName, String methodSig)
        {
            super(api);
            returnCount = 0;
            maxLocalsValue = 0;
            this.methodName = methodName;
            this.methodSig = methodSig;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
        {
            if (name.equals(methodName) && descriptor.equals(methodSig))
            {
                return new MethodVisitor(Opcodes.ASM9)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (isReturn(opcode))
                            returnCount++;
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals)
                    {
                        maxLocalsValue = maxLocals;
                    }
                };
            }
            return null;
        }

        public static boolean isReturn(int opcode)
        {
            return opcode == Opcodes.RETURN
                    || opcode == Opcodes.ARETURN
                    || opcode == Opcodes.FRETURN
                    || opcode == Opcodes.DRETURN
                    || opcode == Opcodes.LRETURN
                    || opcode == Opcodes.IRETURN;
        }

        public static int getMethodReturnCount(ClassReader classReader, String methodName, String methodSig)
        {
            MethodBasicInfoClassVisitor classVisitor = new MethodBasicInfoClassVisitor(ASM_VERSION, methodName, methodSig);
            classReader.accept(classVisitor, 0);
            return classVisitor.returnCount;
        }
        public static int getMaxLocals(ClassReader classReader, String methodName, String methodSig)
        {
            MethodBasicInfoClassVisitor classVisitor = new MethodBasicInfoClassVisitor(ASM_VERSION, methodName, methodSig);
            classReader.accept(classVisitor, 0);
            return classVisitor.maxLocalsValue;
        }
    }

    private static class Modifier
    {
        private final String name;
        private final String descriptor;
        public Modifier(String name, String descriptor)
        {
            this.name = name;
            this.descriptor = descriptor;
        }
    }

    private static class MethodModifier extends Modifier
    {
        private final Consumer<MethodVisitor> atHead;
        private final Consumer<MethodVisitor> atTail;
        private final Consumer<MethodVisitor> atReturn;

        public MethodModifier(String name, String descriptor, Consumer<MethodVisitor> atHead, Consumer<MethodVisitor> atTail, Consumer<MethodVisitor> atReturn)
        {
            super(name, descriptor);
            this.atHead = atHead;
            this.atTail = atTail;
            this.atReturn = atReturn;
        }
    }

    private static class MethodOnInvokeModifier extends Modifier //right before an invoke
    {
        private final String targetOwner;
        private final String targetMethodName;
        private final String targetMethodSig;
        private final Consumer<MethodVisitor> onInvoke;
        public MethodOnInvokeModifier(String name, String descriptor, String targetOwner, String targetMethodName, String targetMethodSig, Consumer<MethodVisitor> onInvoke)
        {
            super(name, descriptor);
            this.targetOwner = targetOwner;
            this.targetMethodName = targetMethodName;
            this.targetMethodSig = targetMethodSig;
            this.onInvoke = onInvoke;
        }

    }

    private static class MethodConstantModifier extends Modifier
    {
        private final Object LDC;
        private final Consumer<MethodVisitor> atLDC;
        public MethodConstantModifier(String name, String descriptor, Object LDC, Consumer<MethodVisitor> atLDC)
        {
            super(name, descriptor);
            this.LDC = LDC;
            this.atLDC = atLDC;
        }
    }

    private static class MethodClassTransformer extends ClassVisitor
    {
        private final ClassReader classReader;
        private final Modifier[] modifiers;
        public MethodClassTransformer(int api, ClassVisitor classVisitor, ClassReader classReader, Modifier[] modifiers)
        {
            super(api, classVisitor);
            this.classReader = classReader;
            this.modifiers = modifiers;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions)
        {
            for (Modifier modifier : modifiers)
            {
                if (!name.equals(modifier.name) || !descriptor.equals(modifier.descriptor)) continue;
                if (modifier instanceof MethodModifier methodModifier)
                {
                    int returnCount = (methodModifier.atTail != null ? MethodBasicInfoClassVisitor.getMethodReturnCount(classReader, name, descriptor) : 0);
                    return new MethodVisitor(api, cv.visitMethod(access, name, descriptor, signature, exceptions))
                    {
                        private int returnIndex = 0;
                        @Override
                        public void visitCode()
                        {
                            if (methodModifier.atHead != null)
                                methodModifier.atHead.accept(mv);
                            mv.visitCode();
                        }

                        @Override
                        public void visitInsn(int opcode)
                        {
                            if (MethodBasicInfoClassVisitor.isReturn(opcode))
                            {
                                if (methodModifier.atReturn != null)
                                    methodModifier.atReturn.accept(mv);
                                if (methodModifier.atTail != null)
                                {
                                    returnIndex++;
                                    if (returnIndex == returnCount)
                                        methodModifier.atTail.accept(mv);
                                }
                            }
                            mv.visitInsn(opcode);
                        }
                    };
                }
                else if (modifier instanceof MethodConstantModifier constantModifier)
                {
                    return new MethodVisitor(api, cv.visitMethod(access, name, descriptor, signature, exceptions))
                    {
                        @Override
                        public void visitLdcInsn(Object value)
                        {
                            if (!constantModifier.LDC.equals(value))
                            {
                                mv.visitLdcInsn(value);
                                return;
                            }
                            constantModifier.atLDC.accept(mv);
                        }
                    };
                }
                else if (modifier instanceof MethodOnInvokeModifier onInvokeModifier)
                {
                    return new MethodVisitor(api, cv.visitMethod(access, name, descriptor, signature, exceptions))
                    {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
                        {
                            if (isInterface
                                    || opcode != Opcodes.INVOKEVIRTUAL
                                    || !name.equals(onInvokeModifier.targetMethodName)
                                    || !descriptor.equals(onInvokeModifier.targetMethodSig))
                            {
                                mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                return;
                            }
                            onInvokeModifier.onInvoke.accept(mv);
                        }
                    };
                }
            }
            return cv.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
