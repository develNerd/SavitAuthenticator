// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("savitauthenticator");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("savitauthenticator")
//      }
//    }

#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_org_savit_savitauthenticator_utils_PreferenceProvider_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "XLM098DKDPWO2020DLDLD";
    return env->NewStringUTF(hello.c_str());
}