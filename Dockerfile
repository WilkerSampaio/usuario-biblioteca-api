FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY build/libs/usuario-biblioteca-api-0.0.1-SNAPSHOT.jar /app/usuario-biblioteca-api.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/usuario-biblioteca-api.jar"]