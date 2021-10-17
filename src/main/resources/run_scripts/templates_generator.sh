#!/bin/bash

HOME_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
CONFIG_FILE=$1

echo ###################################################################
echo Reindexer home folder: $HOME_DIR
echo ###################################################################
echo Configuration file: $CONFIG_FILE
echo #

cp $HOME_DIR/config/reindex.yml $HOME_DIR/client/assets/

java -Dlog4j2.configurationFile=$HOME_DIR/config/log4j2.xml -cp $HOME_DIR/lib/*:$HOME_DIR/bin/* com.dbeast.templates_generator.TemplatesGenerator $HOME_DIR


