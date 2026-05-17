FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app2
 
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app2
COPY --from=build /app2/target/*-shaded.jar app2.jar
CMD ["java", "-jar", "app2.jar"]