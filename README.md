# WebShell
Spring Boot Web Shell

## Dependencies
At least: Java 8 and Maven 3.5

## Build Service Config
mvn package -DskipTests=true

### Windows
java -jar target\webshell-0.0.2-SNAPSHOT.jar

### Linux (service enabled)
./target/webshell-0.0.2-SNAPSHOT.jar start

## Docker build
docker build -t serviceconfig:latest . --build-arg JAR_FILE=./target/webshell-0.0.2-SNAPSHOT.jar

## Docker run
docker run --name webshell -d -p 8111:8001 -v /tmp:/tmp webshell:latest
