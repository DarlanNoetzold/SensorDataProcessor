#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include "tech_noetzold_SensorDataProcessor_service_DataService.h"

JNIEXPORT jdoubleArray JNICALL Java_tech_noetzold_SensorDataProcessor_service_DataService_aggregateData
  (JNIEnv *env, jobject obj, jdoubleArray data) {
    jsize length = (*env)->GetArrayLength(env, data);
    jdouble *elements = (*env)->GetDoubleArrayElements(env, data, 0);

    int blockSize = 5; // Tamanho do bloco para agregação
    int newLength = (length + blockSize - 1) / blockSize; // Tamanho do array resultante

    jdouble *aggregated = (jdouble*)malloc(newLength * sizeof(jdouble));
    for (int i = 0; i < newLength; i++) {
        double sum = 0.0;
        int count = 0;
        for (int j = 0; j < blockSize && (i * blockSize + j) < length; j++) {
            sum += elements[i * blockSize + j];
            count++;
        }
        aggregated[i] = sum / count;
    }

    jdoubleArray result = (*env)->NewDoubleArray(env, newLength);
    (*env)->SetDoubleArrayRegion(env, result, 0, newLength, aggregated);

    free(aggregated);
    (*env)->ReleaseDoubleArrayElements(env, data, elements, 0);
    return result;
}
