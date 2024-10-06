# Use the official OpenJDK base image with Java 17
FROM openjdk:17-jdk-alpine

RUN apk update && apk upgrade --no-cache

EXPOSE 8080

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven executable to the container image
COPY mvnw .
COPY .mvn .mvn

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN chmod +x mvnw && ./mvnw package -DskipTests \
    && mv target/*.jar ./app.jar

# Specify the command to run on container start
CMD ["java", "-jar", "app.jar"]
