# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# 1. Copy root pom
COPY pom.xml .

# 2. Copy module poms (Giữ nguyên cả Application và Container để Maven hiểu cấu trúc)
COPY paymentService/paymentContainer/pom.xml paymentService/paymentContainer/pom.xml
COPY paymentService/paymentApplication/pom.xml paymentService/paymentApplication/pom.xml
COPY common-messaging/common-messaging/pom.xml common-messaging/common-messaging/pom.xml

# 3. Resolve dependencies (SỬA: trỏ vào paymentContainer)
RUN mvn -B -f pom.xml -pl paymentService/paymentContainer -am dependency:go-offline || true

# 4. Copy source code
COPY . .

# 5. Build Package (SỬA: trỏ vào paymentContainer và dùng -DskipTests đúng chính tả)
RUN mvn -B -f pom.xml -pl paymentService/paymentContainer -am -DskipTests package

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# 6. Copy jar file (SỬA: lấy từ folder target của paymentContainer)
COPY --from=build /workspace/paymentService/paymentContainer/target/*.jar app.jar

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]