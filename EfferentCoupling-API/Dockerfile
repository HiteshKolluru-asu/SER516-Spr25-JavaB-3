# Use a base image with JDK
FROM openjdk:21-jdk

# Set working directory
WORKDIR /app

# Copy the JAR dynamically from the Maven build step
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} efferent-coupling-api.jar

# Expose the port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "efferent-coupling-api.jar"]
