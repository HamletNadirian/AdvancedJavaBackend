@echo off
echo Starting build...
call mvn clean package
java -jar .\target\JavaCore-1.0-SNAPSHOT.jar .\src\main\resources genre
if errorlevel 1 (echo JAR failed! Check log.txt && pause)
cd ..
pause