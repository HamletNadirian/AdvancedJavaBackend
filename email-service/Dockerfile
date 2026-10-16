# Build stage
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY src ./src
RUN gradle build -x test --no-daemon -q

# Run stage
FROM openjdk:27-ea-oracle
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
