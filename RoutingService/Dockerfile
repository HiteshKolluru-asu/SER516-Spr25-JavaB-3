# Use OpenJDK 21 as base image
FROM openjdk:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the JAR dynamically from the Maven build step
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} routing-service.jar

# Expose port 8080 for API Gateway
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "routing-service.jar"]
