FROM openjdk:11.0.9-jre-slim

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

## Override it with: java -jar /app.jar
CMD ["java", "-jar", "/app.jar"]

