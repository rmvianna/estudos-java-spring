FROM openjdk:11-jre-slim-buster

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /usr/src/myapp/api-cursos.jar

WORKDIR /usr/src/myapp

CMD ["java", "-Xms128M", "-Xmx128M", "-jar", "api-cursos.jar"]