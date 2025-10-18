# ---- Stage 1: Build the app ----
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy Gradle build files first to leverage caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy the source code and build the jar
COPY . .
RUN ./gradlew clean bootJar --no-daemon

# ---- Stage 2: Run the app ----
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
