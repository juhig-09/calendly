# Use official PostgreSQL image from Docker Hub
FROM postgres:latest

# Set environment variables for PostgreSQL setup
ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=postgres

# Copy initialization scripts to container
# These scripts will be run when the container is initialized
COPY ./init.sql /docker-entrypoint-initdb.d/

# Expose port 5432 for PostgreSQL
EXPOSE 5432
