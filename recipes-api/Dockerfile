FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY pom.xml /app/
WORKDIR /app
RUN mvn dependency:go-offline -B

COPY . /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]