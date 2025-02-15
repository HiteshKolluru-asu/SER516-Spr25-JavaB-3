# Use a lightweight JDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the Maven Wrapper and POM file
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (Layer caching for faster builds)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the rest of the application source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy the built JAR dynamically to the container
RUN cp target/*.jar api-gateway.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
