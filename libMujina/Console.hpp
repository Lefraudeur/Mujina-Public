#pragma once

#ifdef _WIN32
#include <Windows.h>
#endif

#include "Base.hpp"
#include <iostream>

//#define NO_CONSOLE

class Console : public Base
{
public:
	Console(void* dll);
	~Console();

	static void log_warning(const std::string& warning);
	static void log_error(const std::string& error);
	static void log_success(const std::string& success);
private:
	FILE* buff1;
	FILE* buff2;
	FILE* buff3;
	void* dll;
};