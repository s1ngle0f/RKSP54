#FROM openjdk:17-jdk-alpine
#
#WORKDIR /app
#ARG JAR_FILE=target/*.jar
#COPY ./build/libs/RKSP5_3-0.0.1-SNAPSHOT-plain.jar app.jar
#
#CMD ["java", "-jar", "app.jar"]
FROM maven:3.8.6-ibm-semeru-17-focal

ADD . /app
WORKDIR /app

RUN mvn clean install

FROM openjdk:17-oracle
LABEL name="ZEA"

ARG JAR_FILE=/app/target/*.jar
COPY --from=0 ${JAR_FILE} /application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
