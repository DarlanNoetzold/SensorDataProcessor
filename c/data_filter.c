#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "com_example_sensordataprocessor_service_DataService.h"

JNIEXPORT jdoubleArray JNICALL Java_com_example_sensordataprocessor_service_DataService_filterData
  (JNIEnv *env, jobject obj, jdoubleArray data) {
    jsize length = (*env)->GetArrayLength(env, data);
    jdouble *elements = (*env)->GetDoubleArrayElements(env, data, 0);

    // Filtro básico: remover duplicados e valores menores que 0.1
    jdouble *filtered = malloc(length * sizeof(jdouble));
    int count = 0;
    for (int i = 0; i < length; i++) {
        int duplicate = 0;
        for (int j = 0; j < count; j++) {
            if (elements[i] == filtered[j]) {
                duplicate = 1;
                break;
            }
        }
        if (!duplicate && elements[i] > 0.1) {
            filtered[count++] = elements[i];
        }
    }

    jdoubleArray result = (*env)->NewDoubleArray(env, count);
    (*env)->SetDoubleArrayRegion(env, result, 0, count, filtered);

    free(filtered);
    (*env)->ReleaseDoubleArrayElements(env, data, elements, 0);
    return result;
}
