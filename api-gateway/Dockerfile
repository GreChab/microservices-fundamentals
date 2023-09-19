FROM eclipse-temurin:18-alpine
EXPOSE 80
ARG JAR_FILE=build/libs/api-gateway-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]