cmake_minimum_required(VERSION 3.5)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
set(CMAKE_CXX_STANDARD 17)
project(mpv_jni LANGUAGES CXX)

option(IS_RELEASE "Whether this is a release build" OFF)
option(LINK_STATICALLY "Whether this is a release build" OFF)

find_package(PkgConfig REQUIRED)

pkg_check_modules(AVCODEC REQUIRED IMPORTED_TARGET libavcodec)
pkg_check_modules(MPV REQUIRED IMPORTED_TARGET mpv)

file(GLOB SOURCE_FILES CONFIGURE_DEPENDS "src/*.cpp")
add_library(mpv_jni SHARED ${SOURCE_FILES})

target_include_directories(mpv_jni PRIVATE "include" "include/mpv" "include/jni")

if(WIN32)
    target_include_directories(mpv_jni PRIVATE "include/jni/win32")
endif()
if(APPLE)
    # TODO
endif()
if(UNIX AND NOT APPLE)
    target_include_directories(mpv_jni PRIVATE "include/jni/linux")
endif()

add_compile_options(-Werror -fPIC)
add_link_options(-z noexecstack -fvisibility=hidden)
if(LINK_STATICALLY)
    add_link_options(-static)
endif()

target_link_libraries(mpv_jni PRIVATE PkgConfig::AVCODEC PkgConfig::MPV)