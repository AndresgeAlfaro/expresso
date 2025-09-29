@echo off
rem Ejecuta el CLI de Expresso con Maven exec:java
mvn compile exec:java -Dexec.mainClass=expresso.cli.Expressor %*
