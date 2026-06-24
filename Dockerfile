# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies (this layer is cached)
RUN mvn dependency:go-offline -B
COPY backend ./backend
COPY frontend ./frontend
# Build the application, skipping tests to speed up deployment
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/self-improvement-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar file with production profile activated
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
