FROM openjdk:22-jdk-slim
VOLUME /data
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]