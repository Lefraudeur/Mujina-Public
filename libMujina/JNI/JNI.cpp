#include "JNI.hpp"
#include <unordered_map>
#include <thread>
#include "../Console.hpp"

JNI::JNI() :
	jvm(nullptr),
	jvmti_env(nullptr),
	jni_env(nullptr),
	attached_thread(false),
	detach_jvmti(true)
{
	jint vm_count = 0;
	if (JNI_GetCreatedJavaVMs(&jvm, 1, &vm_count) != JNI_OK ||
		!jvm || !vm_count)
	{
		Console::log_error("Failed to get JavaVM");
		return;
	}

	if (jvm->GetEnv((void**)&jni_env, JNI_VERSION_1_8) == JNI_EDETACHED)
	{
		jvm->AttachCurrentThread((void**)&jni_env, nullptr);
		attached_thread = true;
	}
	if (!jni_env)
	{
		Console::log_error("Failed to get jni env");
		return;
	}

	if (jvm->GetEnv((void**)&jvmti_env, JVMTI_VERSION_1_2) != JNI_OK ||
		!jvmti_env)
	{
		Console::log_error("Failed to get JvmtiEnv");
		return;
	}
	_is_error = false;
}

JNI::JNI(JNIEnv* jni_env, jvmtiEnv* jvmti_env) :
	jni_env(jni_env),
	jvmti_env(jvmti_env),
	jvm(nullptr),
	attached_thread(false),
	detach_jvmti(false)
{
	if (!jni_env || !jvmti_env) return;

	jni_env->GetJavaVM(&jvm);

	_is_error = false;
}

JNI::~JNI()
{
	if (_is_error)
		return;

	if (attached_thread)
		jvm->DetachCurrentThread();

	if (detach_jvmti)
		jvmti_env->DisposeEnvironment();
}

JNIEnv* JNI::get_ct_env()
{
	static std::unordered_map<std::thread::id, JNIEnv*> env_cache{};
	try
	{
		return env_cache.at(std::this_thread::get_id());
	}
	catch (...)
	{
		JavaVM* jvm = nullptr;
		JNI_GetCreatedJavaVMs(&jvm, 1, nullptr);
		JNIEnv* env = nullptr;
		if (jvm->GetEnv((void**)&env, JNI_VERSION_1_8) == JNI_EDETACHED)
			jvm->AttachCurrentThread((void**)&env, nullptr);
		if (env)
			env_cache.insert({ std::this_thread::get_id(), env });
		return env;
	}
	return nullptr;
}

JNIEnv* JNI::get_env()
{
	return jni_env;
}

jvmtiEnv* JNI::get_jvmti_env()
{
	return jvmti_env;
}

jclass JNI::find_class_any_cl(std::string_view class_path)
{
	if (_is_error)
		return nullptr;
	JNIEnv* env = get_env();
	if (!env)
		return nullptr;

    jclass* loaded_classes = nullptr;
    jint loaded_classes_count = 0;
    jclass found_class = nullptr;
    jvmti_env->GetLoadedClasses(&loaded_classes_count, &loaded_classes);
    for (jint i = 0; i < loaded_classes_count; ++i)
    {
        char* signature_buffer = nullptr;
		jvmti_env->GetClassSignature(loaded_classes[i], &signature_buffer, nullptr);
        std::string signature = signature_buffer;
		jvmti_env->Deallocate((unsigned char*)signature_buffer);
        signature = signature.substr(1, signature.size() - 2);
        if (signature == class_path)
        {
            found_class = (jclass)env->NewLocalRef(loaded_classes[i]);
            break;
        }
    }
    for (jint i = 0; i < loaded_classes_count; ++i)
    {
		env->DeleteLocalRef(loaded_classes[i]);
    }
	jvmti_env->Deallocate((unsigned char*)loaded_classes);
    return found_class;
}

std::vector<std::string> JNI::get_loaded_classes_name()
{
	std::vector<std::string> classes{};
	if (_is_error)
		return classes;
	JNIEnv* env = get_env();
	if (!env)
		return classes;

	jclass* loaded_classes = nullptr;
	jint loaded_classes_count = 0;
	jclass found_class = nullptr;
	jvmti_env->GetLoadedClasses(&loaded_classes_count, &loaded_classes);
	for (jint i = 0; i < loaded_classes_count; ++i)
	{
		char* signature_buffer = nullptr;
		jvmti_env->GetClassSignature(loaded_classes[i], &signature_buffer, nullptr);
		std::string signature = signature_buffer;
		jvmti_env->Deallocate((unsigned char*)signature_buffer);
		signature = signature.substr(1, signature.size() - 2);
		classes.push_back(signature);
		env->DeleteLocalRef(loaded_classes[i]);
	}
	jvmti_env->Deallocate((unsigned char*)loaded_classes);
	return classes;
}

jobject JNI::get_class_loader(const std::string& class_name)
{
	jclass found = find_class_any_cl(class_name);
	if (!found)
		return nullptr;

	jobject classLoader = nullptr;
	get_jvmti_env()->GetClassLoader(found, &classLoader);
	return classLoader;
}

jclass JNI::defineClass(const jbyte* classBytes, jsize size, jobject class_loader)
{
	JNIEnv* env = get_env();
	if (!env) return nullptr;
	jclass jaclass = env->DefineClass(nullptr, class_loader, classBytes, size);
	if (!jaclass)
	{
		Console::log_error("Failed to define class: is class already loaded ?");
		describe_error();
		return nullptr;
	}
	return jaclass;
}

bool JNI::describe_error()
{
	LocalFrame frame(*this, 1);
	JNIEnv* env = get_env();
	if (env->ExceptionOccurred())
	{
		env->ExceptionDescribe();
		env->ExceptionClear();
		Console::log_error("jni error");
		return true;
	}
	return false;
}

LocalFrame::LocalFrame(JNI& jni, int ref_count) :
	jni(jni)
{
	jni.get_env()->PushLocalFrame(ref_count);
}

LocalFrame::~LocalFrame()
{
	jni.get_env()->PopLocalFrame(nullptr);
}
