@echo off
echo === Compilando proyecto Expresso ===
call mvnw\mvnw.cmd -f pom.xml clean package
pause
