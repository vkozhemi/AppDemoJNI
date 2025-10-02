cmake_minimum_required(VERSION 3.22)

project(imgfilters LANGUAGES CXX)

# C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Library sources
add_library(imgfilters SHARED
        jni/jni_bridge.cpp
        filters/filters.cpp
)

# Include paths (if your headers are under these folders)
target_include_directories(imgfilters
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
)

# NDK libs (use valid variable names; no hyphens)
find_library(LOG_LIB           log)
find_library(ANDROID_LIB       android)
find_library(JNIGRAPHICS_LIB   jnigraphics)

# Warnings
if (CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    target_compile_options(imgfilters PRIVATE -Wall -Wextra -Werror=return-type)
endif()

# Link
target_link_libraries(imgfilters
        PRIVATE
        ${LOG_LIB}
        ${ANDROID_LIB}
        ${JNIGRAPHICS_LIB}
)
