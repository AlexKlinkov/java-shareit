FROM amazoncorretto:11-alpine-jdk
# ENV TZ="Europe/Moscow"
COPY target/*.jar app.jar
WORKDIR ./server/target/*
RUN sh -c 'touch app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]