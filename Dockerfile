FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

ENV DB_URL=jdbc:mysql://localhost:3306/pawlink_db
ENV DB_USER=root
ENV DB_PASSWORD=password

ENTRYPOINT ["java","-jar","/app.jar"]
