@echo off
rem GLang launcher (Windows):
rem   glang <file.glang>   run the program
setlocal

set "JAVA=java"
if defined JAVA_HOME set "JAVA=%JAVA_HOME%\bin\java"

"%JAVA%" -jar "%~dp0..\lib\glang.jar" %*
exit /b %ERRORLEVEL%
