# Use an openjdk image for building the application
FROM openjdk:17-jdk-slim AS builder

RUN apt-get update && apt-get install -y unzip curl

# Set working directory
WORKDIR /app

# Install Gradle 8.8
RUN curl -L https://services.gradle.org/distributions/gradle-8.8-bin.zip -o gradle-8.8-bin.zip
RUN unzip gradle-8.8-bin.zip
RUN rm gradle-8.8-bin.zip  # Clean up the zip file

# Set the environment variables for Gradle
ENV GRADLE_HOME=/app/gradle-8.8
ENV PATH=$PATH:$GRADLE_HOME/bin

# Verify Gradle installation
RUN gradle --version

# Copy Gradle build files
COPY build.gradle settings.gradle /app/
COPY calendly-api/build.gradle /app/calendly-api/
COPY calendly-common/build.gradle /app/calendly-common/

# Copy the rest of the application source code
COPY calendly-api/src /app/calendly-api/src
COPY calendly-common/src /app/calendly-common/src

# Run Gradle build (skipping tests)
WORKDIR /app/calendly-api
RUN gradle clean build -x test

FROM openjdk:17-jdk-slim

# Copy the JAR from the builder image
COPY --from=builder /app/calendly-api/build/libs/calendly-api-1.0.0.jar /app/app.jar

# Set environment variables for PostgreSQL connection
ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=postgres
ENV POSTGRES_HOST=postgres
ENV POSTGRES_PORT=5432

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
