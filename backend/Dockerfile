FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

COPY src src
RUN ./mvnw package -DskipTests -B

EXPOSE 8080

CMD ["java", "-jar", "target/e-commerce-api-0.0.1-SNAPSHOT.jar"]
