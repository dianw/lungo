FROM eclipse-temurin:11-alpine as build
WORKDIR /opt/build
COPY .mvn ./.mvn
COPY mvnw ./mvnw
COPY src/ ./src
COPY pom.xml ./pom.xml
RUN ./mvnw -DskipTests clean package

FROM eclipse-temurin:11-jre-alpine as prod
WORKDIR /opt/lungo
COPY --from=build /opt/build/target/*.jar ./app.jar
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT exec java $JAVA_OPTS -jar ./app.jar
