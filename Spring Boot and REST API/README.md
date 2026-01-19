# Spring Boot and REST API Application

This is a Spring Boot application with REST API for managing movies and producers. It uses PostgreSQL as the database,
Liquibase for database migrations and seeding, and Swagger for API documentation.
Prerequisites
Java JDK 21 or higher
Maven 3.6+
PostgreSQL database server (running locally or accessible)
IDE (optional, e.g., IntelliJ IDEA or Eclipse) for running the application

## Setup and Launch Instructions

### 1. Database Setup

Create the database using PostgreSQL.

```
psql -U postgres
CREATE DATABASE movies_db;
```

### 2. Build and Run the Application

Run the application:
Open the project in your IDE.
Run the  file as a Java application.
```
SpringBootAndRestApiApplication.java
```


Alternatively, run from your IDE:

``` 
mvn spring-boot:run
```

### 3. Application settings

The `application.properties` file is already configured:

```
spring.datasource.username=postgres
spring.datasource.password=hamletnadirian
spring.datasource.url=jdbc:postgresql://localhost:5432/movies_db
...
```

### 4. Verification

After launching the application:

Liquibase will automatically create the necessary tables and populate them with test data on the first run.
Swagger UI will be available at:
http://localhost:8080/swagger-ui/index.html

On the first launch, Liquibase automatically seeds:

Producers:
Christopher Nolan (USA)
Kevin Feige (USA)
James Cameron (USA)

Movies:
Inception (2010) - Sci-Fi, rating 8.8
Jurassic Park (1993) - Adventure, rating 8.1

### Database Configuration Files

#### db.changelog-master.yaml
The file src/main/resources/db/changelog/db.changelog-master.yaml is the master changelog for Liquibase, which manages database schema changes and initial data seeding.

#### example.json
The file src/main/resources/db/example.json contains sample JSON data for testing the import functionality of the
application. This file is used to simulate bulk data imports into the database, particularly for movies and producers.

Structure: The JSON is an array of objects, each representing either a producer or a movie. Example snippet

```
{
    "movies":
       [
         {
            "title": "Sleeping Dogs Lie (a.k.a. Stay)",
            "releaseDate": 2006,
            "producerName": "Steven Spielberg",
            "genre": "Comedy|Drama|Romance",
            "description": "Elegant chiffon blouse perfect for work or outings.",
            "rating": 3
         },....
       ]
}
```
## Integration Tests
Tests for all endpoints are located in Spring Boot and REST API\src\test\java\springboot\restapi\MovieControllerIntegrationTest.java.