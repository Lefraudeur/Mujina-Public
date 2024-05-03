#include "Console.hpp"
#include <thread>

Console::Console(void* dll) : 
	dll(dll),
    buff1(nullptr),
    buff2(nullptr),
    buff3(nullptr)
{
#if defined(_WIN32) && !defined(NO_CONSOLE)

    AllocConsole();
    freopen_s(&buff1, "CONOUT$", "w", stdout);
    freopen_s(&buff2, "CONOUT$", "w", stderr);
    freopen_s(&buff3, "CONIN$", "r", stdin);

    log_success("Init");
#endif

    _is_error = false;
}

Console::~Console()
{
#if defined(_WIN32) && !defined(NO_CONSOLE)
    if (buff1)
        fclose(buff1);
    if (buff2)
        fclose(buff2);
    if (buff3)
        fclose(buff3);
        
    FreeConsole();
#endif

#if defined(_WIN32)
    FreeLibrary((HMODULE)dll);
#endif
}

void Console::log_warning(const std::string& warning)
{
#ifndef NO_CONSOLE
    std::clog << "[!] " + warning + '\n';
#endif
}

void Console::log_error(const std::string& error)
{
#ifndef NO_CONSOLE
    std::cerr << "[-] " + error + '\n'
        << "Pess enter to ignore\n";
    std::cin.ignore();
#endif
}

void Console::log_success(const std::string& success)
{
#ifndef NO_CONSOLE
    std::clog << "[+] " + success + '\n';
#endif
}
