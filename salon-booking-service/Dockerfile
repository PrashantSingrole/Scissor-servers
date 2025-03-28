From maven:3.9.u7-eclipse-temurin-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
