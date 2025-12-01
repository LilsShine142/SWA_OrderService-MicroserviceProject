# Stage 1: build with Maven
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .

COPY paymentApplication/pom.xml paymentApplication/pom.xml

# download dependencies
RUN mvn -B -f pom.xml -pl paymentApplication -am dependency:go-offline || true

# copy full source
COPY . .

# build the payment module (skip tests for faster builds; remove -DskipTests to run tests)
RUN mvn -B -f pom.xml -pl paymentApplication -am -DskipTests package

# Stage 2: runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/paymentApplication/target/*.jar app.jar


EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
