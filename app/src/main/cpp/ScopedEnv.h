
#pragma once

#include <stdlib.h>
#include <jni.h>

class ScopedEnv
{
public:
	ScopedEnv(JavaVM* vm) : m_vm(vm), m_env(0), m_attach(false)
	{
		if(m_vm->GetEnv((void**)&m_env, JNI_VERSION_1_6) != JNI_OK) {
			m_vm->AttachCurrentThread(&m_env, NULL);
			m_attach = true;
		}
	}
	virtual ~ScopedEnv()
	{
		if(m_env->ExceptionCheck()) {
			m_env->ExceptionDescribe();
			m_env->ExceptionClear();
		}
		if(m_attach) {
			m_vm->DetachCurrentThread();
		}
	}
	operator JNIEnv*() { return m_env; }
	JNIEnv* operator->() { return m_env; }
private:
	JavaVM* m_vm;
	JNIEnv* m_env;
	bool m_attach;
};
