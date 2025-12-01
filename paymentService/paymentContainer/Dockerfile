# Stage 1: build with Maven (use Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY paymentApplication/pom.xml paymentApplication/pom.xml
RUN mvn -B -f pom.xml -pl paymentApplication -am dependency:go-offline || true  # Resolve deps
COPY . .
RUN mvn -B -f pom.xml -pl paymentApplication -am -DskipTests package  # Build jar

# Stage 2: runtime image (Java 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/paymentApplication/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]