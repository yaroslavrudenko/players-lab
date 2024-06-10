# Please do not use @latest version as we want to have control over
# image versions, instaed use actual latest versions at the moment.
# Use same OS image for build and runtime.
# Alpine is chosen for its small footprint
# compared to Ubuntu
# base image to build a JRE
FROM amazoncorretto:21-alpine3.19-jdk as jre

ARG APP_BUILD
ARG APP_VERSION
ARG APP_FULL_NAME
# default values
ARG LOG_SOURCE
ARG CONFIG_SOURCE

ENV LOG_SOURCE=$LOG_SOURCE
ENV CONFIG_SOURCE=$CONFIG_SOURCE
ENV APP_FULL_NAME=$APP_FULL_NAME
ENV PLAYERS_CONFIG_SOURCE=$PLAYERS_CONFIG_SOURCE

# required for strip-debug to work
RUN apk add --no-cache binutils
RUN apk add --no-cache curl

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
    --verbose \
    --add-modules ALL-MODULE-PATH \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /customjre

WORKDIR /app
COPY ./players-main/target/$APP_FULL_NAME .

RUN mkdir -p ./runtime
COPY ./players-main/src/main/resources/* ./runtime/
COPY ./scripts/docker-run.sh ./docker-run.sh

# main app image
FROM alpine:latest

RUN apk add --no-cache curl

ARG LOG_SOURCE
ARG CONFIG_SOURCE
ARG APP_FULL_NAME

ENV LOG_SOURCE=$LOG_SOURCE
ENV CONFIG_SOURCE=$CONFIG_SOURCE
ENV APP_FULL_NAME=$APP_FULL_NAME
ENV PLAYERS_CONFIG_SOURCE=$PLAYERS_CONFIG_SOURCE

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=jre /customjre $JAVA_HOME/

WORKDIR /app
RUN mkdir -p ./runtime

COPY --from=jre /app/*.jar ./
COPY --from=jre /app/runtime/* ./runtime/
COPY --from=jre /app/docker-run.sh ./docker-run.sh
RUN chmod +x ./docker-run.sh

EXPOSE 8080 80 8085

# This is for Docker Debug, pause container to make possible to enter it
# ENTRYPOINT ["tail", "-f", "/dev/null"]
CMD ./docker-run.sh