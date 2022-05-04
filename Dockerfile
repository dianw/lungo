FROM openjdk:8-jdk-alpine
VOLUME /lungo
ARG JAR_FILE=*.jar
ADD target/${JAR_FILE} /lungo/app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /lungo/app.jar
