FROM amazoncorretto:11-alpine-jdk
# ENV TZ="Europe/Moscow"
WORKDIR ./gateway/src/*
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]