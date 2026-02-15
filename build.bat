@echo off
REM Build script untuk Democracy Core Plugin
echo ====================================
echo Building Democracy Core Plugin v1.3.0
echo ====================================

set MAVEN_PATH=C:\Users\mohza\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\maven\latest\bin\mvn.cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21

echo.
echo Maven: %MAVEN_PATH%
echo Java: %JAVA_HOME%
echo.

"%MAVEN_PATH%" clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================
    echo BUILD SUCCESS!
    echo ====================================
    echo.
    echo Output JAR: target\Democracy-Core-Plugin-1.3.0.jar
    echo.
) else (
    echo.
    echo ====================================
    echo BUILD FAILED!
    echo ====================================
    echo.
)

pause
