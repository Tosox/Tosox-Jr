FROM openjdk:17-alpine
LABEL authors="leonbcode"
COPY target/tosox-jr.jar /usr/app/
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "tosox-jr.jar"]