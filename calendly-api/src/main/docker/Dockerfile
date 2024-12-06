FROM openjdk:17-jdk-slim

# Set the working directory for the API
WORKDIR /app

# Copy the application JAR file (assuming the JAR is already built)
COPY calendly-api/build/libs/calendly-api-1.0.0.jar app.jar

# Set environment variables for PostgreSQL connection
ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=postgres
ENV POSTGRES_HOST=localhost
ENV POSTGRES_PORT=5432

# Expose port for the API
EXPOSE 8080

# Command to run the API
CMD ["java", "-jar", "app.jar"]

