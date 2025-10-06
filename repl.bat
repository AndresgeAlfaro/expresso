@echo off
rem Ejecuta el REPL de Expresso
mvn compile exec:java -Dexec.mainClass=expresso.cli.Repl
