

@echo off

for %%x in ("%JAVA_HOME%") do set JAVA_HOME=%%~sx
for %%x in ("%DAVINCI3_HOME%") do set DAVINCI3_HOME=%%~sx

if "%1" == "start" (
    echo start Davinci Server
    start "Davinci Server" java -Dfile.encoding=UTF-8 -cp .;%JAVA_HOME%\lib\*;%DAVINCI3_HOME%\lib\*; com.DavinciServerApplication --spring.config.additional-location=file:%DAVINCI3_HOME%\config\application.yml
) else if "%1" == "stop" (
    echo stop Davinci Server
    taskkill /fi "WINDOWTITLE eq Davinci Server"
) else (
    echo please use "run.bat start" or "run.bat stop"
)

pause