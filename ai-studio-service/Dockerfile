FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml /build/pom.xml
COPY src /build/src

RUN mvn -q -DskipTests package

FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /build/target/ai-studio-service-1.0.0.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
