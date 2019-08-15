

@echo off

for %%x in ("%MYSQL_HOME%") do set MYSQL_HOME=%%~sx
for %%x in ("%DAVINCI3_HOME%") do set DAVINCI3_HOME=%%~sx

%MYSQL_HOME%\bin\mysql.exe -h localhost -uroot -proot davinci0.3 < %DAVINCI3_HOME%\bin\davinci.sql