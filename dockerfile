FROM openjdk:8-jdk-alpine

RUN apk add --no-cache bash

ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/*.jar /app

EXPOSE 8080

ENTRYPOINT ["/app"]
