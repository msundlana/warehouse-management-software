# Use the official PostgreSQL image from Docker Hub
FROM postgres:latest

# Set environment variables for PostgreSQL database
ENV POSTGRES_DB warehouse_db
ENV POSTGRES_USER admin
ENV POSTGRES_PASSWORD admin

# Copy the SQL setup script into the container
COPY setup.sql /docker-entrypoint-initdb.d/
