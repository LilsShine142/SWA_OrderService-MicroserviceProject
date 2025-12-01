# Stage 1: build with Maven (use Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# copy root pom so multi-module resolution works
COPY pom.xml .

# copy the module poms used by this service and any shared modules
COPY order-service/order-container/pom.xml order-service/order-container/pom.xml
COPY order-service/order-domain/order-domain-core/pom.xml order-service/order-domain/order-domain-core/pom.xml
COPY order-service/order-domain/order-application-service/pom.xml order-service/order-domain/order-application-service/pom.xml
COPY common-messaging/common-messaging/pom.xml common-messaging/common-messaging/pom.xml

# resolve deps (use the root pom and the module path)
RUN mvn -B -f pom.xml -pl order-service/order-container -am dependency:go-offline || true

# copy full repo and build the module (adjust -pl path if needed)
COPY . .
RUN mvn -B -f pom.xml -pl order-service/order-container -am -DskipTests package  # Build jar

# Stage 2: runtime image (Java 21)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/order-service/order-container/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
