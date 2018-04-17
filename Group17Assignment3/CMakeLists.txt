set(pathToOpenCV C:/opencv/OpenCV-android-sdk/sdk/native)
set(pathToProject C:/Users/josh/Desktop/CSE535/Group17Assignment3)
include_directories(${pathToOpenCV}/jni/include)

# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

include_directories(${pathToOpenCv}/sdk/native/jni/include)

#add_executable(hello ${PROJECT_SOURCE_DIR}/app/src/main/cpp/hello.cpp)
# Specifies a path to native header files.
#include_directories(src/main/cpp/include/)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             ${PROJECT_SOURCE_DIR}/app/src/main/cpp/hello.cpp)

add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( native-lib $\{log-lib} lib_opencv)