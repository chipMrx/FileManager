#include <jni.h>
#include <string>
#include "ScopedEnv.h"


void* createApiCallBack(JNIEnv* env,  jobject clazz);

void requestInitAuth(void* callback);
jstring getServerApi(JNIEnv* env, jboolean isDebug);
jstring getSubHost(JNIEnv* env, jboolean isDebug);
