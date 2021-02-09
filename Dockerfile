FROM openjdk:8-jdk-alpine
VOLUME /mainapi
ARG JAR_FILE=*.jar
ADD target/${JAR_FILE} /mainapi/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /mainapi/app.jar
