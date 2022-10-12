#include "AppManager.h"

class AppManagerCallBack{

public:AppManagerCallBack(JavaVM* vm,  jobject obj, jclass clz)
            : m_vm(vm)
            , m_obj(0)
    {
        ScopedEnv env(m_vm);
        m_obj = obj;
        m_initKeyEncryptionMethodID = env->GetMethodID(clz, "setFlag", "(Ljava/lang/String;)V");
        m_checkIsDebugMethodID = env->GetMethodID(clz, "makeLove", "()Z");
        m_getVersionCodeMethodID = env->GetMethodID(clz, "groupFuckLog", "()I");
        m_getBuildTimeMethodID = env->GetMethodID(clz, "totalBitchLog", "()J");
        m_getVersionNameMethodID = env->GetMethodID(clz, "asshole", "()Ljava/lang/String;");
    }
    virtual ~AppManagerCallBack()
    {
        ScopedEnv env(m_vm);
        if(m_obj) env->DeleteGlobalRef(m_obj);
        m_vm = 0;
        m_obj = 0;
        m_initKeyEncryptionMethodID = 0;
        m_checkIsDebugMethodID = 0;
        m_getVersionCodeMethodID = 0;
        m_getVersionNameMethodID = 0;
        m_getBuildTimeMethodID = 0;
    }

    void initKeyEncryption(){
        try {
            if (m_initKeyEncryptionMethodID == 0) return ;
            ScopedEnv env(m_vm);
            jstring key = env->NewStringUTF("emma311019050189");//9-311019
            env->CallVoidMethod(m_obj, m_initKeyEncryptionMethodID, key);
            env->DeleteLocalRef(key);
        } catch (...) {}
}

    jint getVersionCode() {
        try {
            if (m_getVersionCodeMethodID == 0) return 0;
            ScopedEnv env(m_vm);
            return env->CallIntMethod(m_obj, m_getVersionCodeMethodID);
        } catch (...) {}
        return -1;
    }

    jstring getVersionName() {
        if (m_getVersionNameMethodID == 0) return 0;
        ScopedEnv env(m_vm);
        try {
            return (jstring) env->CallObjectMethod(m_obj, m_getVersionNameMethodID);
        } catch (...) {}
        return nullptr;
    }

    jboolean checkIsDebug()
    {
        if(m_checkIsDebugMethodID == 0) return 0;
        ScopedEnv env(m_vm);
        try{
            return env->CallBooleanMethod(m_obj, m_checkIsDebugMethodID);
        }catch (...){}
        return true;
    }

    jlong getBuildTime()
    {
        if(m_getBuildTimeMethodID == 0) return 0;
        ScopedEnv env(m_vm);
        try {
            return env->CallLongMethod(m_obj, m_getBuildTimeMethodID);
        }catch (...){}
        return -1;
    }

private: AppManagerCallBack()
            : m_vm(0)
            , m_obj(0)
            , m_initKeyEncryptionMethodID(0)
            , m_checkIsDebugMethodID(0)
            , m_getVersionCodeMethodID(0)
            , m_getVersionNameMethodID(0)
            , m_getBuildTimeMethodID(0)
    {
    }
    JavaVM* m_vm;
    jobject m_obj;
    jmethodID m_initKeyEncryptionMethodID;
    jmethodID m_checkIsDebugMethodID;
    jmethodID m_getVersionCodeMethodID;
    jmethodID m_getVersionNameMethodID;
    jmethodID m_getBuildTimeMethodID;
};


void* createAppManagerCallBack(JNIEnv* env, jobject clazz)
{
    JavaVM* vm = nullptr;
    env->GetJavaVM(&vm);
    jclass callbackClass = env->FindClass("com/apcc/framework/AppManager");
    auto* callback = new AppManagerCallBack(vm, clazz, callbackClass);
    callback->initKeyEncryption();
    return callback;
}

jlong requestGetBuildTime(void* callback){
    return ((AppManagerCallBack*)callback)->getBuildTime();
}

jint requestGetBuildCode(void* callback){
    return ((AppManagerCallBack*)callback)->getVersionCode();
}

jboolean requestGetBuildType(void* callback){
    return ((AppManagerCallBack*)callback)->checkIsDebug();
}

jstring requestGetBuildName(void* callback){
    return ((AppManagerCallBack*)callback)->getVersionName();
}