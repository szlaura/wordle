# Stage 1: Build the application
FROM maven:3.8.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/wordle-0.0.1-SNAPSHOT.jar ./wordle.jar
COPY --from=build /app/src/main/resources/dictionary.txt ./

ENTRYPOINT ["java", "-jar", "wordle.jar"]