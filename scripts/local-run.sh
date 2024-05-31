#!/bin/sh

JAVA_OPTS=" -Xms512M"
JAVA_OPTS="$JAVA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
JAVA_OPTS="$JAVA_OPTS -Dlog4j2.enable.threadlocals=true"
JAVA_OPTS="$JAVA_OPTS -Dlog4j2.enable.direct.encoders=true"
JAVA_OPTS="$JAVA_OPTS -Dlog4j2.garbagefree.threadContextMap=true"

JAVA_OPTS="$JAVA_OPTS --add-opens java.base/jdk.internal.misc=ALL-UNNAMED"
JAVA_OPTS="$JAVA_OPTS -Dlog4j.level=INFO"

# Get the parent directory path of the current script
parent_dir=$(dirname "$(dirname "${BASH_SOURCE[0]}")")

CONFIG_FILE=${CONFIG_SOURCE:="${parent_dir}/players-main/src/main/resources/application.yaml"}
LOG_FILE=${LOG_SOURCE:="${parent_dir}/players-main/src/main/resources/spring-log4j2.xml"}
PLAYERS_FILE=${PLAYERS_CONFIG_SOURCE:="${parent_dir}/players-main/src/main/resources/player.csv"}

if [[ ! -f "${CONFIG_FILE}" ]]; then
	echo "mandatory configuration is absent for path ${CONFIG_FILE}"
	exit
fi
if [[ ! -f "${LOG_FILE}" ]]; then
	echo "mandatory log file is absent for path ${LOG_FILE}"
	exit
fi
if [[ ! -f "${PLAYERS_FILE}" ]]; then
	echo "mandatory players file is absent for path ${PLAYERS_FILE}"
	exit
fi

TARGET_DIR=./players-main/target
APP_VERSION=`(sed -n 's,.*<version>\(.*\)</version>.*,\1,p' pom.xml | head -1)`
APP_NAME=`(sed -n 's,.*<artifactId>\(.*\)</artifactId>.*,\1,p' pom.xml | head -1)`
APP_FULL_NAME="${APP_NAME}-${APP_VERSION}.jar"

JAVA_COMMANDS=" --spring.config.location=${CONFIG_FILE}"
JAVA_COMMANDS="$JAVA_COMMANDS --logging.config=${LOG_FILE}"
JAVA_COMMANDS="$JAVA_COMMANDS --players.config.source=${PLAYERS_FILE}"

clear

cat <<"EOF"

██████╗ ██╗      █████╗ ██╗   ██╗███████╗██████╗ ███████╗
██╔══██╗██║     ██╔══██╗╚██╗ ██╔╝██╔════╝██╔══██╗██╔════╝
██████╔╝██║     ███████║ ╚████╔╝ █████╗  ██████╔╝███████╗
██╔═══╝ ██║     ██╔══██║  ╚██╔╝  ██╔══╝  ██╔══██╗╚════██║
██║     ███████╗██║  ██║   ██║   ███████╗██║  ██║███████║
╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚══════╝


EOF

echo "1.  Config (CONFIG_SOURCE): '${CONFIG_SOURCE}'"
echo "2.  Logger (LOG_SOURCE): '${LOG_SOURCE}'"
echo "3.  Players (PLAYERS_CONFIG_SOURCE): '${PLAYERS_CONFIG_SOURCE}'"
echo "4.  App (App Artifact):  '${APP_NAME}'"
echo "5.  App (App full name Artifact):  '${APP_FULL_NAME}'"
echo
echo "Running application pack: $TARGET_DIR/${APP_FULL_NAME}"

java $JAVA_OPTS -jar $TARGET_DIR/$APP_FULL_NAME $JAVA_COMMANDS
