#include <jni.h>
#include <string>
#include "api.h"
#include "AppManager.h"

#define APP_METHOD(METHOD_NAME) \
  Java_com_apcc_framework_AppManager_##METHOD_NAME  // app manager

#define UTILS_METHOD(METHOD_NAME) \
  Java_com_apcc_utils_Util_##METHOD_NAME  // normal

#define B_AUTH_METHOD(METHOD_NAME) \
  Java_com_apcc_api_BasicAuthInterceptor_##METHOD_NAME  // basic auth

#define DATA_METHOD(METHOD_NAME) \
  Java_com_apcc_utils_DataHelper_##METHOD_NAME  // data helper

#ifdef __cplusplus

extern "C" {
#endif
///////////////////////////
JNIEXPORT void JNICALL APP_METHOD(vm)(
        JNIEnv* env, jobject clazz);
///////////////////////////
JNIEXPORT void JNICALL UTILS_METHOD(cappuccino)(
        JNIEnv* env, jobject clazz);
///////////////////////////
JNIEXPORT void JNICALL B_AUTH_METHOD(setVM)(
        JNIEnv* env, jobject clazz);
///////////////////////////
JNIEXPORT jstring JNICALL DATA_METHOD(getServerApi)(
        JNIEnv* env, jobject clazz);

JNIEXPORT jstring JNICALL DATA_METHOD(getSubHost)(
        JNIEnv* env, jobject clazz);
///////////////////////////
#ifdef __cplusplus
}
#endif

jboolean hasCappuccino = false;

void* mAPICallBack = nullptr;
void* mAppManagerCallBack = nullptr;

jlong MAKE_TIME = 0;
jboolean DEBUG = true;
jint VERSION_CODE = 0;
jstring VERSION_NAME = nullptr;
////////////////////////
JNIEXPORT void JNICALL APP_METHOD(vm)(
        JNIEnv* env, jobject clazz){
    if (!hasCappuccino)
        return;
    try{
        if (mAppManagerCallBack == nullptr){
            mAppManagerCallBack = createAppManagerCallBack(env, clazz);
        }
        MAKE_TIME = requestGetBuildTime(mAppManagerCallBack);
        VERSION_CODE = requestGetBuildCode(mAppManagerCallBack);
        DEBUG = requestGetBuildType(mAppManagerCallBack);
        VERSION_NAME = requestGetBuildName(mAppManagerCallBack);

        const char *vsName = env->GetStringUTFChars(VERSION_NAME, 0);
        const char *vsInvalid = env->GetStringUTFChars(env->NewStringUTF("-1"), 0);
        if (MAKE_TIME == -1
        || VERSION_CODE == -1
        || strcmp(vsName, vsInvalid) == 0){
            hasCappuccino = false;
            requestGetBuildCode(mAppManagerCallBack);
        }
    }catch (...){
        mAppManagerCallBack = nullptr;
        hasCappuccino = false;
    }
}
////////////////////////
JNIEXPORT void JNICALL UTILS_METHOD(cappuccino)(
        JNIEnv* env, jobject clazz) {
    hasCappuccino = true;

}
////////////////////////
JNIEXPORT void JNICALL B_AUTH_METHOD(setVM)(
        JNIEnv* env, jobject clazz){
    if (!hasCappuccino)
        return;
    if (mAPICallBack == nullptr){
        mAPICallBack = createApiCallBack(env, clazz);
    }
    requestInitAuth(mAPICallBack);
}

////////////////////////
JNIEXPORT jstring JNICALL DATA_METHOD(getServerApi)(
        JNIEnv* env, jobject clazz){
    if (!hasCappuccino)
        return env->NewStringUTF("https://google.com/");
    return getServerApi(env, DEBUG);
}

JNIEXPORT jstring JNICALL DATA_METHOD(getSubHost)(
        JNIEnv* env, jobject clazz){
    if (!hasCappuccino)
        return env->NewStringUTF("/cloud/");
    return getSubHost(env, DEBUG);
}
////////////////////////