FROM openjdk:27-ea-oracle

WORKDIR /app

COPY target/spring-0.0.1-SNAPSHOT.jar app.jar

RUN useradd -m appuser && chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]