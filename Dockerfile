FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 8080:3000
RUN mkdir /app
COPY --from=build /home/gradle/src/backend/build/libs/*.jar /app/currency-converter.jar
ENTRYPOINT ["java", "-jar", "/app/currency-converter.jar"]