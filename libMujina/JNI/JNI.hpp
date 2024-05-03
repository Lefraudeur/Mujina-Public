#pragma once
#include <JNI/jni.h>
#include <JNI/jvmti.h>
#include "../Base.hpp"
#include <vector>

class JNI : public Base
{
public:
	JNI();
	JNI(JNIEnv* jni_env, jvmtiEnv* jvmti_env);
	~JNI();
	JNIEnv* get_env();
	jvmtiEnv* get_jvmti_env();
	jclass find_class_any_cl(std::string_view class_path);
	std::vector<std::string> get_loaded_classes_name();
	jobject get_class_loader(const std::string& class_name);
	jclass defineClass(const jbyte* classBytes, jsize size, jobject class_loader = nullptr);
	bool describe_error(); //return true if there is an error to describe
	static JNIEnv* get_ct_env();
private:
	JavaVM* jvm;
	jvmtiEnv* jvmti_env;
	JNIEnv* jni_env;
	bool attached_thread;
	bool detach_jvmti;
};

//Every jobject local reference created during the lifetime of LocalFrame
//Will be deleted once LocalFrame is destructed
class LocalFrame : public Base
{
public:
	LocalFrame(JNI& jni, int ref_count = 20);
	~LocalFrame();
private:
	JNI& jni;
};