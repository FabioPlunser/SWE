FROM eclipse-temurin:17

# TODO: Get current Version instead of fixed Version
ARG JAR_FILE=build/libs/swa-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
