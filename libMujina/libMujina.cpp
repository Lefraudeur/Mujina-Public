#ifdef _WIN32
    #include <Windows.h>
#elif __linux__
    #include <X11/Xlib.h>
    #include <X11/Xutil.h>
#endif

#include <thread>
#include <iostream>
#include <sstream>
#include <string>
#include <JNI/jni.h>
#include "Console.hpp"
#include "JarLoader/JarLoader.hpp"
#include "JNI/JNI.hpp"
#include "InjectableJar/InjectableJar.jar.hpp"
#include "Java/Java.hpp"
#include "ClassFileParser/ClassFileParser.hpp"
#include "Transformer/Transformer.hpp"
#include "mappings.hpp"
#include "StringCleaner/StringCleaner.hpp"

#ifdef __linux__
static Display* display = nullptr;
#endif

static bool is_uninject_key_pressed()
{
#ifdef _WIN32
    return GetAsyncKeyState(VK_END);
#elif __linux__
    static KeyCode keycode = XKeysymToKeycode(display, XK_End);

    char key_states[32] = { '\0'};
    XQueryKeymap(display, key_states);

    return ( key_states[keycode << 3] & ( 1 << (keycode & 7) ) );
#endif
}

static void jni_related(void* modaddr)
{
    JNI jni{};
    if (!jni) return;
    JNIEnv* env = jni.get_env();

    {
        LocalFrame frame(jni);

        Java::ClassLoader minecraftClassLoader = Java::ClassLoader(jni.get_class_loader(std::string(Mappings::net_minecraft_client_MinecraftClient)), jni);
        std::ostringstream msg{};
        Console::log_success((std::ostringstream() << "minecraft ClassLoader: " << (void*)minecraftClassLoader).str());


        Java::URLClassLoader mujinaClassLoader = Java::URLClassLoader::new_object(jni, "file:///C:/Windows/win.ini", minecraftClassLoader);
        Console::log_success((std::ostringstream() << "mujina ClassLoader: " << (void*)mujinaClassLoader).str());

        JarLoader jarLoader{ jni, mujinaClassLoader, minecraftClassLoader };
        if (!jarLoader) return;

        {
            LocalFrame frame(jni);
            if (!jarLoader.load_jar(InjectableJar_jar.data(), InjectableJar_jar.size()))
                return;
            const Java::Class& MainClass = mujinaClassLoader.findLoadedClass("io/github/lefraudeur/Main");
            if (!MainClass)
            {
                jni.describe_error();
                return;
            }
            const Java::MethodID& main_ID = MainClass.getStaticMethodID("init", "()V");
            main_ID.invoke<void>(MainClass);
        }
        
        Console::log_success("Loaded Jar");

        Transformer transformer{ jni, minecraftClassLoader };
        if (!transformer) return;
        transformer.retransform();

        Console::log_success("Retransformed");

        while (!is_uninject_key_pressed())
        {
            std::this_thread::sleep_for(std::chrono::milliseconds(50));
        }

        {
            LocalFrame frame(jni);
            const Java::Class& MainClass = mujinaClassLoader.findLoadedClass("io/github/lefraudeur/Main");
            const Java::MethodID& main_ID = MainClass.getStaticMethodID("shutdown", "()V");
            main_ID.invoke<void>(MainClass);
        }
        Console::log_success("Received end key, waiting 1 second");
    }
    Console::log_success("Garbage collecting");

    //lmao
    jni.get_jvmti_env()->ForceGarbageCollection();
    jclass mainClass = jni.find_class_any_cl("io/github/lefraudeur/Main");
    if (!mainClass)
        Console::log_success("Unloaded classes");
    else
        Console::log_error("Failed to unload classes");
}

static void libMain(void* modaddr)
{
#ifdef __linux__
    display = XOpenDisplay(NULL);
#endif

    Console console{ modaddr };
    if (!console) return;
    jni_related(modaddr);
    //StringCleaner::clean_classfiles();

#ifdef __linux__
    XCloseDisplay(display);
#endif
}

#ifdef _WIN32

BOOL WINAPI DllMain(
    HINSTANCE hinstDLL,  // handle to DLL module
    DWORD fdwReason,     // reason for calling function
    LPVOID lpvReserved)  // reserved
{
    // Perform actions based on the reason for calling.
    switch (fdwReason)
    {
    case DLL_PROCESS_ATTACH:
        // Initialize once for each new process.
        // Return FALSE to fail DLL load.
        //creating a new thread might also be uneeded depending on the injector
        std::thread(libMain, hinstDLL).detach();
        break;

    case DLL_THREAD_ATTACH:
        // Do thread-specific initialization.
        break;

    case DLL_THREAD_DETACH:
        // Do thread-specific cleanup.
        break;

    case DLL_PROCESS_DETACH:

        if (lpvReserved != nullptr)
        {
            break; // do not do cleanup if process termination scenario
        }

        // Perform any necessary cleanup.
        break;
    }
    return TRUE;  // Successful DLL_PROCESS_ATTACH.
}

#elif __linux__

void __attribute__((constructor)) onload_linux()
{
    std::thread(libMain, nullptr).detach();
    return;
}
void __attribute__((destructor)) onunload_linux()
{
    return;
}

#endif
