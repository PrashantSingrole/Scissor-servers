FROM openjdk:17-jdk-slim
WORKDIR /app
COPY salon-api-gateway/target/salon-api-gateway.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
