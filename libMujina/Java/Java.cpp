#include "Java.hpp"

Java::Object::Object(jobject instance, JNI& jni) :
	instance(instance),
	jni(jni)
{
	_is_error = !instance;
}

Java::Object::Object(const Object& object) :
	Object(object, jni)
{
}

jobject Java::Object::get_instance() const
{
	return instance;
}

bool Java::Object::is_same(jobject other) const
{
	return instance == other || (instance && other && jni.get_env()->IsSameObject(instance, other) == JNI_TRUE);
}

Java::Object::operator jobject() const
{
	return get_instance();
}

bool Java::Object::operator==(jobject other) const
{
	return is_same(other);
}

Java::Object& Java::Object::operator=(jobject other)
{
	this->instance = other;
	return *this;
}

Java::Object& Java::Object::operator=(Object& other)
{
	this->instance = other;
	return *this;
}

std::string Java::String::to_std_string() const
{
	if (!instance) return std::string();
	int bufferSize = this->modifiedUTF8size();
	char* buffer = new char[bufferSize + 1] { 0 };
	jni.get_env()->GetStringUTFRegion((jstring)instance, 0, size(), buffer);
	buffer[bufferSize] = '\0';
	std::string str = buffer;
	delete[] buffer;
	return str;
}

jsize Java::String::size() const
{
	if (!instance) return 0;
	return jni.get_env()->GetStringLength((jstring)instance);
}

jsize Java::String::modifiedUTF8size() const
{
	if (!instance) return 0;
	return jni.get_env()->GetStringUTFLength((jstring)instance);
}

Java::URLClassLoader Java::URLClassLoader::new_object(JNI& jni, const std::string& search_url, ClassLoader& parent)
{
	JNIEnv* env = jni.get_env();
	if (!env)
		return URLClassLoader(nullptr, jni);

	jclass urlClass = env->FindClass("java/net/URL");
	jmethodID urlContructor = env->GetMethodID(urlClass, "<init>", "(Ljava/lang/String;)V");
	jstring str = env->NewStringUTF(search_url.c_str());
	jobject url = env->NewObject(urlClass, urlContructor, str);
	jobjectArray urls = env->NewObjectArray(1, urlClass, url);
	jclass URLClassLoaderClass = env->FindClass("java/net/URLClassLoader");
	jmethodID URLClassLoaderContructor = env->GetMethodID(URLClassLoaderClass, "<init>", "([Ljava/net/URL;Ljava/lang/ClassLoader;)V");
	jobject URLClassLoader_o = env->NewObject(URLClassLoaderClass, URLClassLoaderContructor, urls, parent.get_instance());

	env->DeleteLocalRef(urlClass);
	env->DeleteLocalRef(url);
	env->DeleteLocalRef(str);
	env->DeleteLocalRef(urls);
	env->DeleteLocalRef(URLClassLoaderClass);

	return Java::URLClassLoader(URLClassLoader_o, jni);
}

void Java::System::gc(JNI& jni)
{
	LocalFrame frame(jni);
	jclass SystemClass = jni.get_env()->FindClass("java/lang/System");
	jmethodID gc_ID = jni.get_env()->GetStaticMethodID(SystemClass, "gc", "()V");
	jni.get_env()->CallStaticVoidMethod(SystemClass, gc_ID);
}

Java::ClassLoader Java::ClassLoader::getSystemClassLoader(JNI& jni)
{
	jclass classLoaderClass = jni.get_env()->FindClass("java/lang/ClassLoader");
	static jmethodID getSystemClassLoader_ID = [&jni, classLoaderClass]()->jmethodID
	{
		if (!classLoaderClass) return nullptr;
		jmethodID mid = jni.get_env()->GetStaticMethodID(classLoaderClass, "getSystemClassLoader", "()Ljava/lang/ClassLoader;");
		return mid;
	}();
	if (!getSystemClassLoader_ID) return ClassLoader(nullptr, jni);
	return ClassLoader(jni.get_env()->CallStaticObjectMethod(classLoaderClass, getSystemClassLoader_ID), jni);
}

void Java::ClassLoader::setSystemClassLoader(JNI& jni, ClassLoader& classLoader)
{
	jclass classLoaderClass = jni.get_env()->FindClass("java/lang/ClassLoader");
	static jfieldID field = [&jni, classLoaderClass]()->jfieldID
	{
		if (!classLoaderClass) return nullptr;
		jfieldID fid = jni.get_env()->GetStaticFieldID(classLoaderClass, "scl", "Ljava/lang/ClassLoader;");
		return fid;
	}();

	jni.get_env()->SetStaticObjectField(classLoaderClass, field, classLoader);
}

Java::ClassLoader Java::ClassLoader::getParent()
{
	static jfieldID field = [this]()->jfieldID
	{
		jclass ClassLoader_class = jni.get_env()->FindClass("java/lang/ClassLoader");
		if (!ClassLoader_class) return nullptr;
		return jni.get_env()->GetFieldID(ClassLoader_class, "parent", "Ljava/lang/ClassLoader;");
	}();

	return Java::ClassLoader(jni.get_env()->GetObjectField(instance, field), jni);
}

void Java::ClassLoader::setParent(ClassLoader& parent)
{
	static jfieldID field = [this]()->jfieldID
	{
		jclass ClassLoader_class = jni.get_env()->FindClass("java/lang/ClassLoader");
		if (!ClassLoader_class) return nullptr;
		return jni.get_env()->GetFieldID(ClassLoader_class, "parent", "Ljava/lang/ClassLoader;");
	}();

	jni.get_env()->SetObjectField(instance, field, parent.get_instance());
}

Java::Class Java::ClassLoader::findClass(std::string class_path)
{
	for (char& c : class_path)
		if (c == '/') c = '.';
	static jmethodID findClass_mid = nullptr;
	if (!findClass_mid)
	{
		jclass classLoaderClass = jni.get_env()->FindClass("java/lang/ClassLoader");
		findClass_mid = jni.get_env()->GetMethodID(classLoaderClass, "findClass", "(Ljava/lang/String;)Ljava/lang/Class;");
		jni.get_env()->DeleteLocalRef(classLoaderClass);
	}
	jstring str = jni.get_env()->NewStringUTF(class_path.c_str());
	jclass found_class = (jclass)jni.get_env()->CallObjectMethod(instance, findClass_mid, str);
	jni.get_env()->DeleteLocalRef(str);
	return Java::Class(found_class, jni);
}

Java::Class Java::ClassLoader::findLoadedClass(std::string class_path)
{
	for (char& c : class_path)
		if (c == '/') c = '.';
	static jmethodID findClass_mid = nullptr;
	if (!findClass_mid)
	{
		jclass classLoaderClass = jni.get_env()->FindClass("java/lang/ClassLoader");
		findClass_mid = jni.get_env()->GetMethodID(classLoaderClass, "findLoadedClass", "(Ljava/lang/String;)Ljava/lang/Class;");
		jni.get_env()->DeleteLocalRef(classLoaderClass);
	}
	jstring str = jni.get_env()->NewStringUTF(class_path.c_str());
	jclass found_class = (jclass)jni.get_env()->CallObjectMethod(instance, findClass_mid, str);
	jni.get_env()->DeleteLocalRef(str);
	return Java::Class(found_class, jni);
}

Java::Class Java::ClassLoader::defineClass(uint8_t class_bytes[], size_t size)
{
	return Java::Class(jni.defineClass((jbyte*)class_bytes, size, instance), jni);
}

Java::Vector Java::ClassLoader::get_classes()
{
	static jfieldID field = [this]()->jfieldID
	{
		jclass ClassLoader_class = jni.get_env()->FindClass("java/lang/ClassLoader");
		if (!ClassLoader_class) return nullptr;
		return jni.get_env()->GetFieldID(ClassLoader_class, "classes", "Ljava/util/Vector;");
	}();
	if (!field) return Vector(nullptr, jni);
	return Vector(jni.get_env()->GetObjectField(instance, field), jni);
}

Java::Class::operator jclass() const
{
	return (jclass)instance;
}

Java::Class Java::Class::forName(JNI& jni, const std::string& name, bool initialize, jobject classLoader)
{
	jclass Class = jni.get_env()->FindClass("java/lang/Class");
	static jmethodID method = [&jni, Class]()->jmethodID
	{
		if (!Class) return nullptr;
		return jni.get_env()->GetStaticMethodID(Class, "forName", "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;");
	}();
	if (!method) return Java::Class(nullptr, jni);

	return Java::Class
	(
		jni.get_env()->CallStaticObjectMethod(Class, method, jni.get_env()->NewStringUTF(name.c_str()), (initialize ? JNI_TRUE : JNI_FALSE), classLoader),
		jni
	);
}

std::string Java::Class::get_jclass_name(jvmtiEnv* env, jclass instance)
{
	char* signature_buffer = nullptr;
	env->GetClassSignature((jclass)instance, &signature_buffer, nullptr);
	std::string signature = signature_buffer;
	env->Deallocate((unsigned char*)signature_buffer);
	signature = signature.substr(1, signature.size() - 2);
	return signature;
}

std::string Java::Class::getName()
{
	return get_jclass_name(jni.get_jvmti_env(), (jclass)instance);
}

Java::MethodID Java::Class::getStaticMethodID(std::string_view name, std::string_view signature) const
{
	return { jni.get_env()->GetStaticMethodID((jclass)instance, name.data(), signature.data()), jni };
}

Java::MethodID Java::Class::getMethodID(std::string_view name, std::string_view signature) const
{
	return { jni.get_env()->GetMethodID((jclass)instance, name.data(), signature.data()), jni };
}

Java::FieldID Java::Class::getStaticFieldID(std::string_view name, std::string_view signature) const
{
	return { jni.get_env()->GetStaticFieldID((jclass)instance, name.data(), signature.data()), jni };
}

Java::FieldID Java::Class::getFieldID(std::string_view name, std::string_view signature) const
{
	return { jni.get_env()->GetFieldID((jclass)instance, name.data(), signature.data()), jni };
}

bool Java::Collection::remove(Object& object)
{
	static jmethodID method = [this]()->jmethodID
	{
		jclass Collection_class = jni.get_env()->FindClass("java/util/Collection");
		if (!Collection_class) return nullptr;
		return jni.get_env()->GetMethodID(Collection_class, "remove", "(Ljava/lang/Object;)Z");
	}();
	if (!method) return false;

	return jni.get_env()->CallBooleanMethod(instance, method, object.get_instance()) == JNI_TRUE;
}

std::vector<Java::Thread> Java::Thread::getAllThreads(JNI& jni)
{
	std::vector<Thread> threads{};

	jint threads_count = 0;
	jthread* threads_buffer = nullptr;
	jni.get_jvmti_env()->GetAllThreads(&threads_count, &threads_buffer);
	if (!threads_count || !threads_buffer) return threads;

	threads.reserve(threads_count);
	for (int i = 0; i < threads_count; ++i)
		threads.push_back(Thread(threads_buffer[i], jni));

	return threads;
}

Java::ClassLoader Java::Thread::getContextClassLoader()
{
	static jmethodID methodID = [this]()->jmethodID
	{
			jclass _class = jni.get_env()->FindClass("java/lang/Thread");
			if (!_class) return nullptr;
			return jni.get_env()->GetMethodID(_class, "getContextClassLoader", "()Ljava/lang/ClassLoader;");
	}();
	if (!methodID) return ClassLoader(nullptr, jni);
	return ClassLoader(jni.get_env()->CallObjectMethod(instance, methodID), jni);
}

void Java::Thread::setContextClassLoader(Java::ClassLoader& classLoader)
{
	static jmethodID methodID = [this]()->jmethodID
	{
			jclass _class = jni.get_env()->FindClass("java/lang/Thread");
			if (!_class) return nullptr;
			return jni.get_env()->GetMethodID(_class, "setContextClassLoader", "(Ljava/lang/ClassLoader;)V");
	}();
	if (!methodID) return;
	jni.get_env()->CallVoidMethod(instance, methodID, classLoader.get_instance());
}

Java::MethodID::MethodID(jmethodID methodID, JNI& jni) :
	jni(jni),
	methodID(methodID)
{
	_is_error = !methodID;
}

Java::MethodID::operator jmethodID() const
{
	return methodID;
}

Java::FieldID::FieldID(jfieldID fieldID, JNI& jni) :
	fieldID(fieldID),
	jni(jni)
{
	_is_error = !fieldID;
}

Java::FieldID::operator jfieldID() const
{
	return fieldID;
}

std::string Java::to_sig(std::string_view cp)
{
	return "L" + std::string(cp) + ";";
}
