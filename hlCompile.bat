@echo off
setlocal

set ROOTPATH=%~dp0
set OUTPUTPATH=%ROOTPATH%\out\production\highlight
javac -d "%OUTPUTPATH%" "%ROOTPATH%\src\Highlight.java"
IF %ERRORLEVEL% EQU 0 echo Success!
