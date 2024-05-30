cmake_minimum_required(VERSION 3.5)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
project(mpv-jni LANGUAGES CXX)

set(CMAKE_BUILD_TYPE Release)
set(OpenGL_GL_PREFERENCE GLVND)
set(CMAKE_CXX_STANDARD 17)

find_package(JNI REQUIRED)
find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)

pkg_check_modules(MPV REQUIRED mpv)
include_directories(${JNI_INCLUDE_DIRS})

file(GLOB SOURCE_FILES CONFIGURE_DEPENDS "*.h" "*.cpp")
add_library(jni SHARED ${SOURCE_FILES})
