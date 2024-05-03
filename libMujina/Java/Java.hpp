#pragma once
#include <cstdint>
#include "../JNI/JNI.hpp"
#include <type_traits>

template<typename T, typename... Rest> inline constexpr bool is_any_of = (std::is_same_v<T, Rest> || ...);

namespace Java
{
	class Object : public Base
	{
	public:
		Object(jobject instance, JNI& jni);
		Object(const Object& object);

		jobject get_instance() const;
		bool is_same(jobject other) const;

		operator jobject() const;
		bool operator==(jobject other) const;
		Object& operator=(jobject other);
		Object& operator=(Object& other);
	protected:
		jobject instance;
		JNI& jni;
	};

	template<typename T> inline constexpr bool is_jni_ref = is_any_of<T, jobject, jstring, jthread, jarray, jobjectArray, jbooleanArray, jbyteArray, jshortArray, jintArray, jfloatArray, jdoubleArray, jlongArray>;
	template<typename T> inline constexpr bool is_jni_type = (is_any_of<T, void, jboolean, jbyte, jshort, jint, jfloat, jdouble, jlong> || is_jni_ref<T>);
	template<typename T> inline constexpr bool is_Object_type = std::is_base_of_v<Object, T>;

	class Class;
	class MethodID : public Base
	{
	public:
		MethodID(jmethodID methodID, JNI& jni);
		operator jmethodID() const;

		template< typename Return = void, typename Receiver, typename... Args,
		typename = std::enable_if_t< is_any_of<Receiver, Object, Class> && ((is_jni_type<Args> || ...) || sizeof...(Args) == 0) > >
		inline std::enable_if_t<(is_jni_type<Return> || is_Object_type<Return>), Return>
		invoke(const Receiver& instance, Args... args) const
		{
			constexpr bool is_static = std::is_same_v<Class, Receiver>;
			if constexpr(std::is_void_v<Return>)
			{
				if constexpr (is_static)
					jni.get_env()->CallStaticVoidMethod(instance, methodID, args...);
				else
					jni.get_env()->CallVoidMethod(instance, methodID, args...);
				return;
			}
			if constexpr(is_Object_type<Return>)
			{
				jobject res = nullptr;
				if constexpr (is_static)
					res = jni.get_env()->CallStaticObjectMethod(instance, methodID, args...);
				else
					res = jni.get_env()->CallObjectMethod(instance, methodID, args...);
				return Return(res, jni);
			}
			if constexpr (std::is_same_v<Return, jboolean>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticBooleanMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallBooleanMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jbyte>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticByteMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallByteMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jshort>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticShortMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallShortMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jint>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticIntMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallIntMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jfloat>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticFloatMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallFloatMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jdouble>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticDoubleMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallDoubleMethod(instance, methodID, args...);
			}
			if constexpr (std::is_same_v<Return, jlong>)
			{
				if constexpr (is_static)
					return jni.get_env()->CallStaticLongMethod(instance, methodID, args...);
				else
					return jni.get_env()->CallLongMethod(instance, methodID, args...);
			}
			if constexpr (is_jni_ref<Return>)
			{
				if constexpr (is_static)
					return (Return)jni.get_env()->CallStaticObjectMethod(instance, methodID, args...);
				else
					return (Return)jni.get_env()->CallObjectMethod(instance, methodID, args...);
			}
		}
	private:
		JNI& jni;
		jmethodID methodID;
	};

	class FieldID : public Base
	{
	public:
		FieldID(jfieldID fieldID, JNI& jni);
		operator jfieldID() const;

		template< typename Return, typename Receiver, 
		typename = std::enable_if_t<is_any_of<Receiver, Object, Class>> >
		inline std::enable_if_t<((is_jni_type<Return> || is_Object_type<Return>) && !std::is_void_v<Return>), Return>
		get(const Receiver& instance) const
		{
			constexpr bool is_static = std::is_same_v<Class, Receiver>;
			if constexpr (is_Object_type<Return>)
			{
				jobject res = nullptr;
				if constexpr (is_static)
					res = jni.get_env()->GetStaticObjectField(instance, fieldID);
				else
					res = jni.get_env()->GetObjectField(instance, fieldID);
				return Return(res, jni);
			}
			if constexpr (std::is_same_v<Return, jboolean>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticBooleanField(instance, fieldID);
				else
					return jni.get_env()->GetBooleanField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jbyte>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticByteField(instance, fieldID);
				else
					return jni.get_env()->GetByteField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jshort>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticShortField(instance, fieldID);
				else
					return jni.get_env()->GetShortField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jint>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticIntField(instance, fieldID);
				else
					return jni.get_env()->GetIntField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jfloat>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticFloatField(instance, fieldID);
				else
					return jni.get_env()->GetFloatField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jdouble>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticDoubleField(instance, fieldID);
				else
					return jni.get_env()->GetDoubleField(instance, fieldID);
			}
			if constexpr (std::is_same_v<Return, jlong>)
			{
				if constexpr (is_static)
					return jni.get_env()->GetStaticLongField(instance, fieldID);
				else
					return jni.get_env()->GetLongField(instance, fieldID);
			}
			if constexpr (is_jni_ref<Return>)
			{
				if constexpr (is_static)
					return (Return)jni.get_env()->GetStaticObjectField(instance, fieldID);
				else
					return (Return)jni.get_env()->GetObjectField(instance, fieldID);
			}
		}
	private:
		JNI& jni;
		jfieldID fieldID;
	};

	class Class : public Object
	{
	public:
		using Object::Object;
		operator jclass() const;
		static Java::Class forName(JNI& jni, const std::string& name, bool initialize, jobject classLoader);
		static std::string get_jclass_name(jvmtiEnv* env, jclass instance);
		std::string getName(); //format: java/lang/Object

		MethodID getStaticMethodID(std::string_view name, std::string_view signature) const;
		MethodID getMethodID(std::string_view name, std::string_view signature) const;
		FieldID getStaticFieldID(std::string_view name, std::string_view signature) const;
		FieldID getFieldID(std::string_view name, std::string_view signature) const;
	};

	class String : public Object
	{
	public:
		using Object::Object;
		std::string to_std_string() const;
		jsize size() const;
	private:
		jsize modifiedUTF8size() const;
	};

	class Collection : public Object
	{
	public:
		using Object::Object;
		bool remove(Object& object);
	};

	class Vector : public Collection
	{
	public:
		using Collection::Collection;
	};

	class ClassLoader : public Object
	{
	public:
		using Object::Object;
		using  Object::operator=;
		static ClassLoader getSystemClassLoader(JNI& jni);
		static void setSystemClassLoader(JNI& jni, ClassLoader& classLoader);
		
		ClassLoader getParent();
		void setParent(ClassLoader& parent);
		Java::Class findClass(std::string class_path);
		Java::Class findLoadedClass(std::string class_path);
		Java::Class defineClass(uint8_t class_bytes[], size_t size);
		Vector get_classes();
	};

	class URLClassLoader : public ClassLoader
	{
	public:
		using ClassLoader::ClassLoader;
		static URLClassLoader new_object(JNI& jni, const std::string& search_url, ClassLoader& parent);
	};

	class System : public Object
	{
	public:
		using Object::Object;
		static void gc(JNI& jni);
	};

	class Thread : public Object
	{
	public:
		using Object::Object;
		static std::vector<Thread> getAllThreads(JNI& jni);

		Java::ClassLoader getContextClassLoader();
		void setContextClassLoader(Java::ClassLoader& classLoader);
	};

	std::string to_sig(std::string_view cp);
}