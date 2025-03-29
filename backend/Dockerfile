FROM maven:3.9.7 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/target/*.jar server.jar

EXPOSE 8080

CMD ["java", "-jar", "server.jar"]