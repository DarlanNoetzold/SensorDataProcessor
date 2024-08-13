#include <jni.h>
#include <stdio.h>
#include <math.h>
#include "tech_noetzold_SensorDataProcessor_service_DataService.h"

JNIEXPORT jdoubleArray JNICALL Java_tech_noetzold_SensorDataProcessor_service_DataService_compressData
  (JNIEnv *env, jobject obj, jdoubleArray data) {
    jsize length = (*env)->GetArrayLength(env, data);
    jdouble *elements = (*env)->GetDoubleArrayElements(env, data, 0);

    // Lista temporária para armazenar os dados comprimidos
    jdouble *compressed = (jdouble *) malloc(length * sizeof(jdouble));
    int compressedIndex = 0;

    // Compressão simples: eliminar valores consecutivos repetidos
    for (int i = 0; i < length; i++) {
        if (i == 0 || elements[i] != elements[i - 1]) {
            compressed[compressedIndex++] = elements[i];
        }
    }

    // Criar um novo array com o tamanho do array comprimido
    jdoubleArray result = (*env)->NewDoubleArray(env, compressedIndex);
    (*env)->SetDoubleArrayRegion(env, result, 0, compressedIndex, compressed);

    // Liberar memória
    (*env)->ReleaseDoubleArrayElements(env, data, elements, 0);
    free(compressed);

    return result;
}
