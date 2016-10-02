@echo off
setlocal

set classpath=%~dp0
java -classpath %classpath% Highlight %*
