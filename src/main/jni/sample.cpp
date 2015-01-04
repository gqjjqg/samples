
#include <jni.h>
#include <stdio.h>

static jint setUserCallBack(JNIEnv *env, jobject object, jobject cb);

static JNINativeMethod gMethods[] = {
    {"setUserCallBack", "(Lcom/example/sample/jni/IJNICallback;)I",(void*)setUserCallBack}
};

const char* JNI_NATIVE_INTERFACE_CLASS = "com/example/sample/jni/JNIActivity";

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved){

    JNIEnv *env = NULL;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_4)){
        return JNI_ERR;
    }

    jclass cls = env->FindClass(JNI_NATIVE_INTERFACE_CLASS);
    if (cls == NULL){
        return JNI_ERR;
    }

    jint nRes = env->RegisterNatives(cls, gMethods, sizeof(gMethods)/sizeof(gMethods[0]));
    if (nRes < 0){
        return JNI_ERR;
    }

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved){

   JNIEnv *env = NULL;
   if (vm->GetEnv((void**)&env, JNI_VERSION_1_4)){
       return;
   }

   jclass cls = env->FindClass(JNI_NATIVE_INTERFACE_CLASS);
   if (cls == NULL){
       return;
   }
   jint nRes = env->UnregisterNatives(cls);
}

jint setUserCallBack(JNIEnv *env, jobject object, jobject cb)
{
	jclass jclsProcess = env->GetObjectClass(cb);
	jmethodID jmidProcess = env->GetMethodID(jclsProcess, "callTest","(II)I");
	env->CallIntMethod(cb, jmidProcess, 12, 20);
	return 0;
}
