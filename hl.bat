@echo off
setlocal

set ROOTPATH=%~dp0
set OUTPUTPATH=%ROOTPATH%\out\production\highlight
java -classpath %OUTPUTPATH% Highlight %*
