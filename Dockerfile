# --- Stage 1: Build the application using Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven descriptor first to leverage Docker cache
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source code and build the project
COPY src ./src
RUN mvn -q -DskipTests clean package


# --- Stage 2: Create runtime image ---
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy only the generated jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Optional JVM options (empty by default)
ENV JAVA_OPTS=""

# Expose API port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

