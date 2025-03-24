FROM openjdk:17
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
RUN cp target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
