# Build stage: build the jar with Maven
FROM maven:3.9.2-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage: smaller image with only the jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
VOLUME /tmp
ENTRYPOINT ["java","-jar","/app.jar"]
