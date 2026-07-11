FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew
COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=build /workspace/build/libs/*.jar /app/app.jar
EXPOSE 8009
ENV SERVER_PORT=8009 \
    SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/app.db \
    JWT_SECRET=docker-dev-secret
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
