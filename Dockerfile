# Usar uma imagem baseada no Debian para compilar os arquivos C
FROM debian:bullseye-slim AS builder

# Instalar GCC para compilar os arquivos C
RUN apt-get update && apt-get install -y build-essential

# Definir o diretório de trabalho para o código-fonte
WORKDIR /app

# Copiar o código-fonte do C para o contêiner
COPY c/*.c /app/c/
COPY c/*.h /app/c/

# Compilar os arquivos C para criar as bibliotecas compartilhadas
RUN mkdir -p /app/libs \
    && gcc -shared -o /app/libs/libdata_filter.so -fPIC /app/c/data_filter.c \
    && gcc -shared -o /app/libs/libdata_compression.so -fPIC /app/c/data_compression.c \
    && gcc -shared -o /app/libs/libdata_aggregation.so -fPIC /app/c/data_aggregation.c

# Compilar a aplicação Java usando a imagem openjdk:22-jdk com Maven
FROM openjdk:22-jdk AS maven_build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o código fonte para o contêiner
COPY . /app

# Compilar o projeto Java
RUN ./mvnw clean install

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
