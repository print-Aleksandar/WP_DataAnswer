FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw

# download dependencies first
RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests



CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]