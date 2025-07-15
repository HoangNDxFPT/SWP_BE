# ===== BUILD STAGE =====
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Build project và tạo JAR file (bỏ qua test để build nhanh)
RUN mvn clean package -DskipTests && rm -rf /root/.m2/repository


# ===== RUNTIME STAGE =====
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy file JAR từ stage build sang
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.war demo.war

# Expose cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "demo.war"]