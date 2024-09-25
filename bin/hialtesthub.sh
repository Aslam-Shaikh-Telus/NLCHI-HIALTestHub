#!/bin/bash

# Change to the directory where the script is located
#cd "$(dirname "$0")"

# Set the path to your Spring Boot JAR file
APP_PATH=/opt2/apps/hialtesthub

# Set the path to the Java executable
JAVA_PATH=$APP_PATH/jdk/openlogic-openjdk-11.0.22+7-linux-x64/bin/java

# Set the path to your Spring Boot JAR file
JAR_PATH=$APP_PATH/HialTestHub.jar

# Set the path to the application.properties file
PROPERTIES_PATH=$APP_PATH/config/application.properties

# Set the path to the server.log file
LOG_PATH=$APP_PATH/logs/server.log

# Function to start the application
start_application() {
  $JAVA_PATH -jar $JAR_PATH > $LOG_PATH 2>&1 &
#  /home/webmethods/hialtesthub/jdk/openlogic-openjdk-11.0.22+7-linux-x64/bin/java -jar /home/webmethods/hialtesthub/HialTestHub.jar
  echo "Application started.."
  tail -f $LOG_PATH
}

# Function to stop the application
stop_application() {
  # Set the application process name (adjust as needed)
  APP_PROCESS_NAME=HialTestHub.jar

  # Find and kill the application process
  PID=$(ps -ef | grep "$APP_PROCESS_NAME" | grep -v grep | awk '{print $2}')
  if [ -z "$PID" ]; then
    echo "Application is not running."
  else
    kill -9 $PID
    echo "Application stopped."
  fi
}

# Check the input argument
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 [start|stop]"
  exit 1
fi

# Process the input argument
case "$1" in
  "start")
    start_application
    ;;
  "stop")
    stop_application
    ;;
  *)
    echo "Invalid argument. Usage: $0 [start|stop]"
    exit 1
    ;;
esac