FROM openjdk:17-jdk-slim AS BUILD_STAGE

WORKDIR /home/app
COPY . .

RUN chmod 755 ./gradlew
RUN cat gradlew > gradlew_temp
RUN sed 's/\r$//' gradlew_temp > gradlew
RUN ./gradlew clean build

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=BUILD_STAGE /home/app/build/libs/jirabot-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]
