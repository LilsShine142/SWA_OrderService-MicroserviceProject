# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy common-messaging first and build it
COPY common-messaging /app/common-messaging
RUN cd common-messaging/common-messaging && mvn clean install -DskipTests

# Copy payment-service source
COPY paymentService /app/paymentService

# Build payment-service
WORKDIR /app/paymentService
RUN mvn clean install -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/paymentService/paymentContainer/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

