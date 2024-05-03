#pragma once
#include <vector>
#include "../Base.hpp"
#include "../JNI/JNI.hpp"
#include "../miniz/miniz.h"
#include <string>
#include <memory>
#include "../Java/Java.hpp"

class JarLoader : public Base
{
public:
	JarLoader(JNI& jni, Java::ClassLoader& classLoader, Java::ClassLoader& parent);
	~JarLoader();

	bool load_jar(const uint8_t jar_bytes[], size_t bytes_number);
	bool load_jar(const std::string& jar_path);

private:

	class ClassBytes : public Base
	{
	public:
		ClassBytes(const std::string& class_name, const std::string& super_name, const std::vector<std::string>& interfaces_names, unsigned char* bytes, size_t size);

		~ClassBytes();

		bool define(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader);

		bool is_defined = false;
	private:

		bool define_super(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader);
		bool define_interfaces(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader);

		std::string class_name;
		std::string super_name;
		std::vector<std::string> interfaces_names;
		unsigned char* bytes;
		size_t size;
	};

	struct OriginalThreadData
	{
		Java::Thread thread;
		Java::ClassLoader originalClassLoader;
	};

	JNI& jni;
	Java::ClassLoader& classLoader;
	Java::ClassLoader originalSystemClassLoader;
	std::vector<OriginalThreadData> originalThreadData{};


	bool update_thread_context_classloaders(Java::ClassLoader& parent);
	void restore_thread_context_classloaders();
	bool load_jar_from_archive(mz_zip_archive& archive);
};