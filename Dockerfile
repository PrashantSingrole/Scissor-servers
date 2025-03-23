FROM openjdk:17
WORKDIR /app
COPY java -jar my-app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
