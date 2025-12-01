# Build stage
FROM maven:3.9-eclipse-temurin-23 AS build

WORKDIR /app

# Copy common-messaging first and build it
COPY common-messaging /app/common-messaging
RUN cd common-messaging/common-messaging && mvn clean install -DskipTests

# Copy order-service source
COPY order-service /app/order-service

# Build order-service
WORKDIR /app/order-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/order-service/order-container/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

