FROM amazoncorretto:11-alpine-jdk
# ENV TZ="Europe/Moscow"
ARG JAR_FILE=./target/shareit-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]