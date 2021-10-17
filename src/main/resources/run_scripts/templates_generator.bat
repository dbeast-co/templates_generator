@echo off

set HOME_DIR=%~dp0
set CONFIG_FILE=%~1
cd %HOME_DIR%

ECHO ###################################################################
ECHO Templates generator home folder: %HOME_DIR%
ECHO ###################################################################
ECHO Configuration file: %CONFIG_FILE%
ECHO #
copy %HOME_DIR%\config\templates_generator.yml %HOME_DIR%\client\assets

java -Dlog4j2.configurationFile=%HOME_DIR%\config\log4j2.xml -cp %HOME_DIR%\lib\*;%HOME_DIR%\bin\* com.dbeast.templates_generator.TemplatesGenerator %HOME_DIR%

PAUSE