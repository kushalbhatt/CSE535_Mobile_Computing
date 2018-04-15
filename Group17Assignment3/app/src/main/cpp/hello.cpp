//
// Created by kushal on 4/15/2018.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_racheldedinsky_group17assignment3_MainActivity_stringFromJNI(JNIEnv *env,
                                                                              jobject instance) {

    // TODO
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}