FROM eclipse-temurin:18-alpine
EXPOSE 8080
ARG JAR_FILE=build/libs/resource-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
