#ifdef _WIN32
#include <Windows.h>
#endif
#include "StringCleaner.hpp"
#include "../miniz/miniz.h"
#include <string>
#include <iostream>
#include "../InjectableJar/InjectableJar.jar.hpp"
#include <vector>
#include <string_view>
#include "../ClassFileParser/ClassFileParser.hpp"

//IGNORE

namespace
{
	std::vector<void*> pattern_scan(const std::vector<std::string_view>& patterns);
}

void StringCleaner::clean_classfiles()
{
	std::vector<std::string_view> patterns = 
	{
		"io/github/lefraudeur/modules",
		"io/github/lefraudeur/events",
		"io/github/lefraudeur/Patcher",
		"io.github.lefraudeur.modules",
		"io.github.lefraudeur.events",
		"io.github.lefraudeur.Patcher",
		"$github$lefraudeur",
		"github/lefraudeur",
		"AimAssist"
	};
	return;
}

#ifdef _WIN32

namespace
{
	std::vector<void*> pattern_scan(const std::vector<std::string_view>& patterns)
	{
		SYSTEM_INFO sys_info{};
		GetSystemInfo(&sys_info);

		std::vector<void*> results{};
		MEMORY_BASIC_INFORMATION memInfo{};
		for (uint8_t* ptr = (uint8_t*)sys_info.lpMinimumApplicationAddress;
			ptr < sys_info.lpMaximumApplicationAddress && VirtualQuery(ptr, &memInfo, sizeof(MEMORY_BASIC_INFORMATION));
			ptr = (uint8_t*)memInfo.BaseAddress + memInfo.RegionSize
			)
		{
			if (memInfo.Protect != PAGE_READWRITE || memInfo.State != MEM_COMMIT || memInfo.Type != MEM_PRIVATE) continue;
			std::string_view view((char*)memInfo.BaseAddress, memInfo.RegionSize);
			for (const std::string_view& pattern : patterns)
			{
				for (size_t size = view.find(pattern); size != std::string_view::npos; size = view.find(pattern, size + 1))
				{
					void* found = (uint8_t*)memInfo.BaseAddress + size;
					if (found == pattern.data())
						continue;
					memset(found, 0, pattern.size());
					results.push_back(found);
				}
			}
		}
		return results;
	}
}
#endif
