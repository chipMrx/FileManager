#include <jni.h>
#include <string>
#include "ScopedEnv.h"


void* createAppManagerCallBack(JNIEnv* env, jobject clazz);

jlong requestGetBuildTime(void* callback);
jint requestGetBuildCode(void* callback);
jboolean requestGetBuildType(void* callback);
jstring requestGetBuildName(void* callback);