#!/usr/bin/env sh
# Lightweight mvnw that uses Docker Maven image when system Maven is missing.
MAVEN_IMAGE=${MAVEN_IMAGE:-maven:3.9.5-eclipse-temurin-17}
docker run --rm -v "$PWD":/usr/src/mymaven -w /usr/src/mymaven "$MAVEN_IMAGE" mvn "$@"
