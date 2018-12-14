#include <jni.h>
#include <string>
#include <iostream>
#include "sdptransform.hpp"

std::string jstring2String(JNIEnv *env, jstring jStr) {
    if (!jStr)
        return "";

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte *pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *) pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return ret;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_parse(
        JNIEnv *env,
        jobject /* this */,
        jstring sdp) {
    std::string sdpStr = jstring2String(env, sdp);
    std::string sdpObject = sdptransform::parse(sdpStr).dump();
    return env->NewStringUTF(sdpObject.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_parseParams(
        JNIEnv *env,
        jobject /* this */,
        jstring params) {
    std::string paramsStr = jstring2String(env, params);
    std::string paramsObject = sdptransform::parseParams(paramsStr).dump();
    return env->NewStringUTF(paramsObject.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_parsePayloads(
        JNIEnv *env,
        jobject /* this */,
        jstring payloads) {
    std::string payloadsStr = jstring2String(env, payloads);
    std::vector<int> payloadsVector = sdptransform::parsePayloads(payloadsStr);
    std::stringstream payloadsObject;
    std::copy(payloadsVector.begin(), payloadsVector.end(), std::ostream_iterator<int>(payloadsObject, " "));
    return env->NewStringUTF(payloadsObject.str().c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_parseImageAttributes(
        JNIEnv *env,
        jobject /* this */,
        jstring params) {
    std::string paramsStr = jstring2String(env, params);
    std::string imageAttributesObject = sdptransform::parseImageAttributes(paramsStr).dump();
    return env->NewStringUTF(imageAttributesObject.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_parseSimulcastStreamList(
        JNIEnv *env,
        jobject /* this */,
        jstring streams) {
    std::string streamsStr = jstring2String(env, streams);
    std::string streamsObject = sdptransform::parseSimulcastStreamList(streamsStr).dump();
    return env->NewStringUTF(streamsObject.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_dingsoft_sdptransform_SdpBridge_write(
        JNIEnv *env,
        jobject /* this */,
        jstring sdp) {
    std::string sdpStr = jstring2String(env, sdp);
    json session = json::parse(sdpStr);
    std::string sdpObject = sdptransform::write(session);
    return env->NewStringUTF(sdpObject.c_str());
}