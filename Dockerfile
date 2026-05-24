FROM eclipse-temurin:25-jre-alpine
VOLUME /tmp
COPY target/webshell-*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
