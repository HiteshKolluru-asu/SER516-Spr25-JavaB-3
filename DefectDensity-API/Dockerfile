# Use OpenJDK 21 as base image
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/*.jar app.jar

# Expose port 8083 (or the port your Spring Boot app uses)
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

