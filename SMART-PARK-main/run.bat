@echo off
echo ============================================================
echo  ParkSmart Hotel Parking System
echo ============================================================
cd /d "%~dp0"

if not exist bin mkdir bin

echo [1/2] Compiling all files...
javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src\com\parksmart\*.java Admin\*.java
if errorlevel 1 ( echo ERROR: Compile failed. & pause & exit /b 1 )

echo [2/2] Launching ParkSmart...
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" com.parksmart.ParkSmartApp

pause