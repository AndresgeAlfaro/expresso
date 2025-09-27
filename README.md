# Expresso (Java + ANTLR4)

Este proyecto es una migración a **Java** del intérprete/transpilador Expresso originalmente hecho en Node.js.  
Está construido con **Maven** y usa **ANTLR4** para generar el lexer y parser a partir de la gramática `Expr.g4`.

---

## 📦 Requisitos

- **Java 17 o superior** (recomendado instalar [Temurin](https://adoptium.net/) u OpenJDK)  
- **Apache Maven** (versión 3.6+)

Verifica tus versiones:

```bash
java -version
mvn -v
```

---

## ⚙️ Instalación y Compilación

1. Descarga y descomprime el proyecto:
   ```
   expresso_maven_exec/
   ```

2. Entra a la carpeta del proyecto:
   ```bash
   cd expresso_maven_exec
   ```

3. Compila con Maven:
   ```bash
   mvn clean compile
   ```

Esto generará las clases de ANTLR (`ExprLexer`, `ExprParser`, etc.) y compilará todo el código Java.

---

## ▶️ Ejecución del REPL

El proyecto ya está configurado con el **plugin `exec-maven-plugin`**, por lo que puedes correr el REPL directamente con:

```bash
mvn exec:java
```

Ejemplo de uso:

```
>>> Expresso REPL iniciado (escriba .exit para salir)
> 2+3*4
14
> (10-4)/2
3
> -5+8
3
> .exit
```

---

## 📂 Estructura del Proyecto

```
expresso_maven_exec/
 ├── pom.xml                           # Configuración de Maven
 └── src/
     └── main/
         ├── antlr4/com/expresso/Expr.g4   # Gramática ANTLR
         └── java/com/expresso/
             ├── Repl.java                 # REPL (punto de entrada)
             ├── ParseEvaluate.java        # Orquesta parser + evaluador
             ├── EvalAst.java              # Evaluador de AST
             ├── EvalVisitor.java          # Visitor para evaluación
             ├── AstBuilder.java           # Convierte ParseTree a AST
             ├── util/StdIO.java           # Utilidades de entrada/salida
             └── ast/                      # Nodos AST
                 ├── Ast.java
                 ├── Binary.java
                 ├── Literal.java
                 ├── Variable.java
                 └── Visitor.java
```

---

## 🛠️ Scripts Opcionales

Para facilitar la ejecución:

### Windows
Crea un archivo `run.bat` en la carpeta del proyecto con este contenido:

```bat
@echo off
mvn exec:java
pause
```

### Linux / Mac
Crea un archivo `run.sh`:

```bash
#!/bin/bash
mvn exec:java
```

y dale permisos de ejecución:

```bash
chmod +x run.sh
```

---

## ✨ Autor

Proyecto académico migrado a Java usando **ANTLR4 + Maven**.  
Permite evaluar expresiones aritméticas con soporte para `+`, `-`, `*`, `/`, paréntesis y unarios negativos.
