#include "JarLoader.hpp"
#include "../ClassFileParser/ClassFileParser.hpp"
#include "../Java/Java.hpp"
#include "../Console.hpp"


JarLoader::JarLoader(JNI& jni, Java::ClassLoader& classLoader, Java::ClassLoader& parent) :
	jni(jni),
	classLoader(classLoader),
	originalSystemClassLoader(nullptr, jni)
{
	if (!jni) return;

	//if (!update_thread_context_classloaders(parent)) return;
	originalSystemClassLoader = Java::ClassLoader::getSystemClassLoader(jni).get_instance();
	Java::ClassLoader::setSystemClassLoader(jni, classLoader);

	_is_error = false;
}

JarLoader::~JarLoader()
{
	//restore_thread_context_classloaders();
	Java::ClassLoader::setSystemClassLoader(jni, originalSystemClassLoader);
}

bool JarLoader::update_thread_context_classloaders(Java::ClassLoader& parent)
{
	for (Java::Thread& thread : Java::Thread::getAllThreads(jni))
	{
		Java::ClassLoader original_classLoader = thread.getContextClassLoader();
		if (!original_classLoader.is_same(parent))
			continue;
		originalThreadData.push_back({ thread, original_classLoader });
		thread.setContextClassLoader(classLoader);
	}
	return true;
}

void JarLoader::restore_thread_context_classloaders()
{
	for (OriginalThreadData& data : originalThreadData)
	{
		data.thread.setContextClassLoader(data.originalClassLoader);
	}
}

bool JarLoader::load_jar_from_archive(mz_zip_archive& archive)
{
	std::vector<std::unique_ptr<ClassBytes>> classes_to_define{};

	//const std::vector<std::string>& already_defined = jni.get_loaded_classes_name();

	mz_uint file_number = mz_zip_reader_get_num_files(&archive);
	for (mz_uint i = 0; i < file_number; i++)
	{
		if (!mz_zip_reader_is_file_supported(&archive, i) || mz_zip_reader_is_file_a_directory(&archive, i)) continue;

		char str[512] = { 0 };
		mz_zip_reader_get_filename(&archive, i, str, 512);
		std::string filename(str);

		if (filename.substr(filename.size() - 6) == ".class")
		{
			size_t classBytes_size = 0;
			unsigned char* classBytes = (unsigned char*)mz_zip_reader_extract_to_heap(&archive, i, &classBytes_size, 0);
			if (!classBytes)
			{
				Console::log_error("Failed to extract " + filename);
				return false;
			}

			ClassFileParser parser(classBytes, classBytes_size);
			const std::string& class_name = parser.get_class_name();
			const std::string& super_name = parser.get_super_class_name();
			const std::vector<std::string>& interfaces_names = parser.get_interfaces_names();
			std::unique_ptr<ClassBytes> cb = std::make_unique<ClassBytes>(class_name, super_name, interfaces_names, classBytes, classBytes_size);
			if (cb->is_error() || class_name == "module-info" /*|| std::find(already_defined.begin(), already_defined.end(), class_name) != already_defined.end() */)
				continue;
			classes_to_define.push_back(std::move(cb));
		}
	}

	for (std::unique_ptr<ClassBytes>& classBytes : classes_to_define)
	{
		if (classBytes->is_defined)
			continue;
		if (!classBytes->define(classes_to_define, jni, classLoader))
			return false;
	}

	return true;
}


bool JarLoader::load_jar(const uint8_t jar_bytes[], size_t bytes_number)
{
	mz_zip_archive archive{};
	if (!mz_zip_reader_init_mem(&archive, jar_bytes, bytes_number, 0))
	{
		Console::log_error("Incorrect jar format");
		return false;
	}

	if (!load_jar_from_archive(archive))
	{
		mz_zip_reader_end(&archive);
		Console::log_error("Could not load jar");
		return false;
	}

	mz_zip_reader_end(&archive);
	return true;
}

bool JarLoader::load_jar(const std::string& jar_path)
{
	mz_zip_archive archive{};
	if (!mz_zip_reader_init_file(&archive, jar_path.c_str(), 0))
	{
		Console::log_error("Incorrect jar path/format");
		return false;
	}

	if (!load_jar_from_archive(archive))
	{
		mz_zip_reader_end(&archive);
		Console::log_error("Could not load jar");
		return false;
	}

	Console::log_success("Successfully loaded jar");
	mz_zip_reader_end(&archive);
	return true;
}

JarLoader::ClassBytes::ClassBytes(const std::string& class_name, const std::string& super_name, const std::vector<std::string>& interfaces_names, unsigned char* bytes,
	size_t size) :
	class_name(class_name),
	super_name(super_name),
	interfaces_names(interfaces_names),
	bytes(bytes),
	size(size)
{
	_is_error = class_name.empty() || !bytes || !size;
}

JarLoader::ClassBytes::~ClassBytes()
{
	if (bytes)
		mz_free(bytes);
}

bool JarLoader::ClassBytes::define(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader)
{
	if (is_defined)
		return true;
	if (!define_super(to_define, jni, classLoader))
		return false;
	if (!define_interfaces(to_define, jni, classLoader))
		return false;

	Java::Class defined = classLoader.defineClass(bytes, size);
	if (!defined)
		return false;
	Console::log_success("defined: " + class_name);
	//classLoader.get_classes().remove(defined);
	is_defined = true;
	return true;
}

bool JarLoader::ClassBytes::define_super(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader)
{
	if (super_name.empty())
		return true;

	for (std::unique_ptr<ClassBytes>& cb : to_define)
	{
		if (!cb->is_defined && cb->class_name == super_name)
		{
			return cb->define(to_define, jni, classLoader);
		}
	}

	return true;
}

bool JarLoader::ClassBytes::define_interfaces(std::vector<std::unique_ptr<ClassBytes>>& to_define, JNI& jni, Java::ClassLoader& classLoader)
{
	if (interfaces_names.empty())
		return true;

	for (const std::string& interface_name : interfaces_names)
	{
		for (std::unique_ptr<ClassBytes>& cb : to_define)
		{
			if (!cb->is_defined && cb->class_name == interface_name)
			{
				if(!cb->define(to_define, jni, classLoader)) return false;
			}
		}
	}

	return true;
}
