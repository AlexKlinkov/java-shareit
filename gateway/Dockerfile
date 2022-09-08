FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=target/shareit-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]