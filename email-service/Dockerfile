FROM openjdk:27-ea-oracle

WORKDIR /app

COPY build/libs/email-service-0.0.1-SNAPSHOT.jar app.jar

RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]