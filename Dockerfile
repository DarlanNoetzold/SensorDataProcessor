# Etapa de construção do JAR
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . /app
RUN mvn clean install

# Usar uma imagem baseada no Debian com OpenJDK para compilar e executar a aplicação Java
FROM debian:bullseye-slim AS builder

# Instalar o OpenJDK e o GCC para compilar os arquivos C
RUN apt-get update && apt-get install -y openjdk-17-jdk build-essential

# Definir o diretório de trabalho para o código-fonte
WORKDIR /app

# Configurar a variável de ambiente JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Copiar o código-fonte do C para o contêiner
COPY c/*.c /app/c/
COPY c/*.h /app/c/

# Compilar os arquivos C para criar as bibliotecas compartilhadas
RUN mkdir -p /app/libs \
    && gcc -shared -o /app/libs/libdata_filter.so -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" /app/c/data_filter.c \
    && gcc -shared -o /app/libs/libdata_compression.so -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" /app/c/data_compression.c \
    && gcc -shared -o /app/libs/libdata_aggregation.so -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" /app/c/data_aggregation.c

# Nova etapa para criar a imagem final
FROM openjdk:22-jdk

# Copiar as bibliotecas compiladas para a nova imagem
COPY --from=builder /app/libs /app/libs

# Copiar o arquivo JAR da aplicação para o contêiner gerado na etapa de build
COPY --from=build /app/target/SensorDataProcessor-0.0.1-SNAPSHOT.jar /app/SensorDataProcessor-0.0.1-SNAPSHOT.jar

# Configurar o caminho da biblioteca compartilhada
ENV LD_LIBRARY_PATH="/app/libs"

# Expor a porta que a aplicação usa
EXPOSE 8082

ENTRYPOINT ["java", "-Djava.library.path=/app/libs", "-jar", "/app/SensorDataProcessor-0.0.1-SNAPSHOT.jar"]
