#!/bin/bash

TG_HOME_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
#CONFIG_FILE=$1

echo ###################################################################
echo Templates generator home folder: TG_HOME_DIR
echo ###################################################################
#echo Configuration file: $CONFIG_FILE
echo #

cp "$TG_HOME_DIR/config/templates_generator.yml" "$TG_HOME_DIR/client/assets/"

java -Dlog4j2.configurationFile="$TG_HOME_DIR/config/log4j2.xml" -cp "$TG_HOME_DIR/lib/*":"$TG_HOME_DIR/bin/*" com.dbeast.templates_generator.TemplatesGenerator "$TG_HOME_DIR"
