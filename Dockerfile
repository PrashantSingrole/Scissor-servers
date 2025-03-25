FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY salon-api-gateway/mvnw .
COPY salon-api-gateway/mvnw.cmd .
COPY salon-api-gateway/.mvn .mvn
COPY salon-api-gateway/pom.xml .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY salon-api-gateway/src /app/src
RUN ./mvnw clean package -DskipTests
COPY /app/target/salon-api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
