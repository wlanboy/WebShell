![Java CI with Maven](https://github.com/wlanboy/WebShell/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

# WebShell
Spring Boot based webfrontend for local shell access

## Dependencies
At least: Java 11 and Maven 3.5

## Change passwords for users "user" and "test"
- https://github.com/wlanboy/WebShell/blob/main/src/main/resources/application.yml
- use a BCryptPasswordEncoder to generate your own password hashes

## Build Service Config
- mvn package -DskipTests=true

### Windows
- java -jar target\webshell-0.1.2-SNAPSHOT.jar

### Linux (service enabled)
- ./target/webshell-0.1.2-SNAPSHOT.jar start

## Docker Hub
https://hub.docker.com/r/wlanboy/webshell

## Docker build
- docker build -t serviceconfig:latest . --build-arg JAR_FILE=./target/webshell-0.1.2-SNAPSHOT.jar

## Docker run
- docker run --name webshell -d -p 8080:8001 -v /tmp:/tmp wlanboy/webshell:latest
