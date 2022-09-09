FROM amazoncorretto:11-alpine-jdk
# ENV TZ="Europe/Moscow"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]