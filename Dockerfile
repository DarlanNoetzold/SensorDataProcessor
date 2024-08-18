# Usar uma imagem baseada no Debian para compilar os arquivos C
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

# Compilar a aplicação Java usando a imagem openjdk:22-jdk com Maven
FROM openjdk:22-jdk AS maven_build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o código fonte para o contêiner
COPY . /app

# Dar permissão de execução ao script mvnw
RUN chmod +x ./mvnw

# Compilar o projeto Java
RUN ./mvnw clean install -DskipTests

# Nova etapa para criar a imagem final
FROM openjdk:22-jdk

# Copiar as bibliotecas compiladas para a nova imagem
COPY --from=builder /app/libs /app/libs

# Copiar o arquivo JAR da aplicação para o contêiner
COPY --from=maven_build /app/target/SensorDataProcessor-0.0.1-SNAPSHOT.jar /app/SensorDataProcessor-0.0.1-SNAPSHOT.jar

# Configurar o caminho da biblioteca compartilhada
ENV LD_LIBRARY_PATH="/app/libs"

# Expor a porta que a aplicação usa
EXPOSE 8082

ENTRYPOINT ["java", "-Djava.library.path=/app/libs", "-jar", "/app/SensorDataProcessor-0.0.1-SNAPSHOT.jar"]
