FROM maven:3.6.0-jdk-11-slim AS build

ARG version=1.0-SNAPSHOT

WORKDIR /home/app/

COPY core /core
COPY discord /discord
COPY model /model
COPY persistence /persistence
COPY pom.xml /
COPY lombok.config /

RUN mvn clean package -DskipTests --no-transfer-progress

FROM openjdk:11-jre-slim

ARG version=1.0-SNAPSHOT

COPY --from=build /discord/target/discord-1.0-SNAPSHOT.jar discord.jar

RUN mkdir /conf/

ENTRYPOINT java -jar discord.jar
