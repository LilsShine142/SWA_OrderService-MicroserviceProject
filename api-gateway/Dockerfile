# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy common-messaging first and build it
COPY common-messaging /app/common-messaging
RUN cd common-messaging/common-messaging && mvn clean install -DskipTests

# Copy api-gateway source
COPY api-gateway /app/api-gateway

# Build api-gateway
WORKDIR /app/api-gateway
RUN mvn clean install -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/api-gateway/target/*.jar app.jar

# Expose port
EXPOSE 8083

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
