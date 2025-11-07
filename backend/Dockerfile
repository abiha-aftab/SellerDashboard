FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -B -ntp dependency:go-offline
COPY src src
RUN ./mvnw -B -ntp -o package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --no-create-home app
COPY --from=build /app/target/backend-*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
