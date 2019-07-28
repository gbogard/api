FROM hseeberger/scala-sbt:8u212_1.2.8_2.12.8 as build-deps

WORKDIR /usr/src/app

COPY build.sbt ./
COPY application ./application
COPY domain ./domain
COPY infrastructure ./infrastructure
COPY library ./library
COPY project ./project

RUN sbt infrastructure/assembly

FROM mcr.microsoft.com/java/jdk:11u3-zulu-alpine
COPY --from=build-deps /usr/src/app/infrastructure/target/scala-2.12 /usr/app
CMD ["java", "-jar", "/usr/app/lambdacademy.jar"]