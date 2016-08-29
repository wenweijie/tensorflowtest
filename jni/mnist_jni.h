#ifndef ORG_TENSORFLOW_JNI_MNIST_JNI_H_  // NOLINT
#define ORG_TENSORFLOW_JNI_MNIST_JNI_H_  // NOLINT

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif	// __cplusplus

#define MNIST_METHOD(METHOD_NAME) \
  Java_org_tensorflow_demo_classifier_MnistClassifier_##METHOD_NAME  // NOLINT

JNIEXPORT jint JNICALL
MNIST_METHOD(initializeMnist)(
    JNIEnv* env, jobject thiz, jobject java_asset_manager,
    jstring model, jint numclasses, jint inputsize);

JNIEXPORT jint JNICALL
MNIST_METHOD(detectDigit)(
    JNIEnv* env, jobject thiz, jintArray image);

#ifdef __cplusplus
}	// extern "C"
#endif	// __cplusplus

#endif
