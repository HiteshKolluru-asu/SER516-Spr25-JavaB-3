# Use a base image with JDK
FROM openjdk:21-jdk
# Set working directory
WORKDIR /app
# Copy the JAR dynamically from the Maven build step
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} afferent-coupling-api.jar
# Expose the port
EXPOSE 8081
# Run the application
ENTRYPOINT ["java", "-jar", "afferent-coupling-api.jar"]
