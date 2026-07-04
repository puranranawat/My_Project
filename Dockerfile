# ===================================================================
# Stage 1: Build
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

LABEL maintainer="devsecops"
LABEL description="Employee Management System - Build Stage"

WORKDIR /app

# Copy POM and download dependencies first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===================================================================
# Stage 2: Run
# ===================================================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="devsecops"
LABEL description="Employee Management System"
LABEL version="1.0.0"

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/employee-management-system-1.0.0.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Environment variables for production
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Health check using actuator endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application with JVM tuning for containers
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
