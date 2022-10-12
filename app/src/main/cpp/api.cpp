#include "api.h"

class APICallBack{

public:APICallBack(JavaVM* vm,  jobject obj, jclass clz)
            : m_vm(vm)
            , m_obj(0)
    {
        ScopedEnv env(m_vm);
        m_obj = obj;
        m_initAuthMethodID = env->GetMethodID(clz, "initAuth", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    }
    virtual ~APICallBack()
    {
        ScopedEnv env(m_vm);
        if(m_obj) env->DeleteGlobalRef(m_obj);
        m_vm = 0;
        m_obj = 0;
        m_initAuthMethodID = 0;
    }

    void initAuth()
    {
        if(m_initAuthMethodID == 0) return;
        ScopedEnv env(m_vm);
        jstring acc = env->NewStringUTF("AnPhatCS");
        jstring pass = env->NewStringUTF("AP@12345678");
        jstring header = env->NewStringUTF("Authorization");
        env->CallVoidMethod(m_obj, m_initAuthMethodID, acc, pass, header);
        env->DeleteLocalRef(acc);
        env->DeleteLocalRef(pass);
    }

private: APICallBack()
            : m_vm(0)
            , m_obj(0)
            , m_initAuthMethodID(0)
    {
    }
    JavaVM* m_vm;
    jobject m_obj;
    jmethodID m_initAuthMethodID;
};


void* createApiCallBack(JNIEnv* env,  jobject clazz)
{
    JavaVM* vm = nullptr;
    env->GetJavaVM(&vm);
    jclass callbackClass = env->FindClass("com/apcc/api/BasicAuthInterceptor");
    auto* callback = new APICallBack(vm, clazz, callbackClass);
    return callback;
}

void requestInitAuth(void* callback){
    ((APICallBack*)callback)->initAuth();
}

jstring getServerApi(JNIEnv* env, jboolean isDebug){
    if (isDebug){
        //return env->NewStringUTF( "http://192.168.2.26:8080/");
        return env->NewStringUTF( "http://anphatcs.com/");
    } else{
        return env->NewStringUTF( "http://anphatcs.com/");
    }
}

jstring getSubHost(JNIEnv* env, jboolean isDebug){
    if (isDebug){
        //return env->NewStringUTF( "/anphatcs/api/");
        return env->NewStringUTF( "/api/");
    } else{
        return env->NewStringUTF( "/api/");
    }
}
