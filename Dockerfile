FROM gradle:8.8.0-jdk21 as build
LABEL authors="evrenbayrak"

WORKDIR /app

COPY gradle/ gradle/
COPY build.gradle settings.gradle gradlew ./
COPY src ./src

RUN ./gradlew --no-daemon build

# Eclipse Temurin base image for Java 21
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -XX:MaxRAMPercentage=70"

EXPOSE 8092

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]