@echo off

set TG_HOME_DIR=%~dp0
set TG_HOME_DIR=%TG_HOME_DIR:~0,-1%
rem set CONFIG_FILE=%~1
cd %HOME_DIR%

ECHO ###################################################################
ECHO Templates generator home folder: %TG_HOME_DIR%
ECHO ###################################################################
rem ECHO Configuration file: %CONFIG_FILE%
ECHO #
copy "%TG_HOME_DIR%\config\templates_generator.yml" "%TG_HOME_DIR%\client\assets"

java -Dlog4j2.configurationFile="%TG_HOME_DIR%\config\log4j2.xml" -cp "%TG_HOME_DIR%\lib\*";"%TG_HOME_DIR%\bin\*" com.dbeast.templates_generator.TemplatesGenerator "%TG_HOME_DIR%"

PAUSE
