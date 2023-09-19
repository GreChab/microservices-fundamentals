FROM eclipse-temurin:18-alpine
EXPOSE 8761
ARG JAR_FILE=build/libs/eureka-server-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]