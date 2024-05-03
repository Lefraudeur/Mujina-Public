# CMakeList.txt : CMake project for Mujina, include source and define
# project specific logic here.
#
cmake_minimum_required (VERSION 3.8)

# Enable Hot Reload for MSVC compilers if supported.
if (POLICY CMP0141)
  cmake_policy(SET CMP0141 NEW)
  set(CMAKE_MSVC_DEBUG_INFORMATION_FORMAT "$<IF:$<AND:$<C_COMPILER_ID:MSVC>,$<CXX_COMPILER_ID:MSVC>>,$<$<CONFIG:Debug,RelWithDebInfo>:EditAndContinue>,$<$<CONFIG:Debug,RelWithDebInfo>:ProgramDatabase>>")
endif()

project ("Mujina")


file(GLOB APP_SOURCES "libMujina/*/*.cpp" "libMujina/*/*.c" "libMujina/*.cpp" "libMujina/*.c" "libMujina/*.hpp" "libMujina/*.h" "libMujina/*/*.hpp" "libMujina/*/*.h")

add_library (libMujina SHARED ${APP_SOURCES})
target_link_directories(libMujina PUBLIC "libMujina/lib")
target_include_directories(libMujina PUBLIC "libMujina/include")
set_property(TARGET libMujina PROPERTY CXX_STANDARD 20)
target_link_libraries(libMujina PUBLIC "jvm" #[["ixwebsocket" "z" "ssl" "crypto"]])
if(WIN32)
	target_link_libraries(libMujina PUBLIC "shlwapi" "WS2_32" "CRYPT32")
	target_include_directories(libMujina PUBLIC "libMujina/include/JNI/win32")
elseif(UNIX)
	target_link_libraries(libMujina PUBLIC "X11")
	target_include_directories(libMujina PUBLIC "libMujina/include/JNI/linux")
endif()