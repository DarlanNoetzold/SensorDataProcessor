FROM openjdk:22-jdk

WORKDIR /app

COPY target/SensorDataProcessor-0.0.1-SNAPSHOT.jar /app/SensorDataProcessor-0.0.1-SNAPSHOT.jar

COPY c/*.dll /usr/java/openjdk-22/bin

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/SensorDataProcessor-0.0.1-SNAPSHOT.jar"]
