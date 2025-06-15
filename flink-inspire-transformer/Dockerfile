FROM maven:3.9.3-eclipse-temurin-17 AS inspire-models-builder
WORKDIR /build/inspire-models

COPY inspire-models/pom.xml .
RUN mvn dependency:go-offline

COPY inspire-models/src ./src
RUN mvn clean install

FROM maven:3.9.3-eclipse-temurin-17 AS flink-builder
WORKDIR /build/flink-inspire-transformer

COPY --from=inspire-models-builder /root/.m2 /root/.m2

COPY flink-inspire-transformer/pom.xml .
RUN mvn dependency:go-offline

COPY flink-inspire-transformer/src ./src
RUN mvn clean package

FROM flink:1.20-java17
COPY --from=flink-builder \
     /build/flink-inspire-transformer/target/flink-inspire-transformer-1.0.0.jar \
     /opt/flink/usrlib/flink-inspire-transformer.jar