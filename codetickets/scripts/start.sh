#!/bin/bash

if [ ! -d "target" ]; then
  printf "Compiling project for the first time. Please wait\n";
  mvn clean package > build.log
  printf "Compilation finished. Check build.log for any issues\n";
fi

if [ ! -f "target/codetickets.jar" ]; then
  printf "Jar file not found. Please check if you have Maven and Java 21 properly installed and run this script again\n"
  exit 1;
fi

set -o allexport
source default.env
set +o allexport

printf "Running application. Press CTRL+C to abort executing\n\n"
java -jar target/codetickets.jar