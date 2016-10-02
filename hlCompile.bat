@echo off
setlocal

set classpath=%~dp0
javac "%classpath%\Highlight.java" && (
  echo Success!
)
