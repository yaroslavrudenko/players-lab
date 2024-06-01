#!/bin/sh

JAVA_COMMANDS=" --spring.config.location=${CONFIG_SOURCE:=/app/runtime/application.yaml}"
JAVA_COMMANDS="$JAVA_COMMANDS --logging.config=${LOG_SOURCE:=/app/runtime/spring-log4j2.xml}"
JAVA_COMMANDS="$JAVA_COMMANDS --players.config.source=${PLAYERS_CONFIG_SOURCE:=/app/runtime/player.csv}"

clear

cat <<"EOF"

██████╗ ██╗      █████╗ ██╗   ██╗███████╗██████╗ ███████╗
██╔══██╗██║     ██╔══██╗╚██╗ ██╔╝██╔════╝██╔══██╗██╔════╝
██████╔╝██║     ███████║ ╚████╔╝ █████╗  ██████╔╝███████╗
██╔═══╝ ██║     ██╔══██║  ╚██╔╝  ██╔══╝  ██╔══██╗╚════██║
██║     ███████╗██║  ██║   ██║   ███████╗██║  ██║███████║
╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝


EOF

echo "1. Checking Curl"
curl -V
echo
echo "2.  Config (CONFIG_SOURCE): '${CONFIG_SOURCE}'"
echo "3.  Logger (LOG_SOURCE): '${LOG_SOURCE}'"
echo "4.  Players (PLAYERS_CONFIG_SOURCE): '${PLAYERS_CONFIG_SOURCE}'"
echo "5.  App (App full name Artifact):  '${APP_FULL_NAME}'"
echo
echo "Running application pack: ${APP_FULL_NAME}"

java $JAVA_OPTS -jar $APP_FULL_NAME $JAVA_COMMANDS
