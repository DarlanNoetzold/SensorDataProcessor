#include <jni.h>
#include <stdio.h>
#include <math.h>
#include "tech_noetzold_SensorDataProcessor_service_DataService.h"

JNIEXPORT jdoubleArray JNICALL Java_tech_noetzold_SensorDataProcessor_service_DataService_compressData
  (JNIEnv *env, jobject obj, jdoubleArray data) {
    jsize length = (*env)->GetArrayLength(env, data);
    jdouble *elements = (*env)->GetDoubleArrayElements(env, data, 0);

    // Compressão simples: arredondar para duas casas decimais
    for (int i = 0; i < length; i++) {
        elements[i] = round(elements[i] * 100.0) / 100.0;
    }

    jdoubleArray result = (*env)->NewDoubleArray(env, length);
    (*env)->SetDoubleArrayRegion(env, result, 0, length, elements);

    (*env)->ReleaseDoubleArrayElements(env, data, elements, 0);
    return result;
}
