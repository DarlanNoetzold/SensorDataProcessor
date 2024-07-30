FROM openjdk:22-jdk
COPY target/SensorDataProcessor-0.0.1-SNAPSHOT.jar /app/SensorDataProcessor-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/SensorDataProcessor-0.0.1-SNAPSHOT.jar"]