# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy common-messaging first and build it
COPY common-messaging /app/common-messaging
RUN cd common-messaging/common-messaging && mvn clean install -DskipTests

# Copy restaurant-service source
COPY restaurant-service /app/restaurant-service

# Build restaurant-service
WORKDIR /app/restaurant-service
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/restaurant-service/restaurant-container/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

