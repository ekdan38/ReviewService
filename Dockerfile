# Base image: Java 17
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/ReviewService-0.0.1-SNAPSHOT.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the port your application runs on
EXPOSE 8080
