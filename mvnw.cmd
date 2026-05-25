@echo off
REM Lightweight mvnw.cmd that runs Maven inside Docker if local mvn is missing
set MAVEN_IMAGE=maven:3.9.5-eclipse-temurin-17
docker run --rm -v "%cd%":/usr/src/mymaven -w /usr/src/mymaven %MAVEN_IMAGE% mvn %*