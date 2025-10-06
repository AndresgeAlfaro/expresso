## EXPRESSO - SPRINT MEDIANO

Versión: 5/10/2025
Curso: EIF400 – Paradigmas de Programación (II-2025)

Autores:  
- Andrés Alfaro Ramírez - 119190404  
- Rafael Blanco Badilla - 119310876  
- Marquely Núñez Morales - 118830724  
- Randy Núñez Vargas - 119100297  

Grupo: 01-1pm

---
# Descripcion
# Expresso (Java + ANTLR4)

Expresso es un mini-lenguaje diseñado para capturar lo escensial de la programacion funcional (FP) mediante una sintaxis reducida pero expresiva.
Este proyecto es una migración a *Java* del intérprete/transpilador Expresso originalmente hecho en Node.js y de los comandos transpile, build y run del sprint inicial de expresso.  
Está construido con *Maven* y usa *ANTLR4* para generar el lexer y parser a partir de la gramática Expr.g4.
Permite evaluar expresiones aritméticas, asignar variables, imprimir resultados y trabajar con lambdas de manera provisional.
Permite leer codigo expresso de un archivo y tambien posee un repl que simula un shell.
---

## OBJETIVOS DEL PROYECTO
- Desarrollar un transpilador de Expresso a Java (jdk 23+).  
- Crear una herramienta CLI expressor que permita lo siguiente:  
  1. Transpilar (transpile)  
  2. Compilar (build)  
  3. Ejecutar (run)  
- Implementar un parser en *ANTLR4* y un *minityper* para validaciones semánticas.    

---

## Requisitos

- SO: Windows 10/11
- Lenguaje: Java 23+


## Dependencias del traspilador

- Parser: Antlr4
- Gestion de dependencias: Maven
- CLI: Basado en Picocli


---

## Instalación y Compilación

1. Descarga y descomprime el proyecto:
   
   expresso/
   

2. Entra a la carpeta del proyecto:
   bash
   cd .\expresso\

3. Instala Maven a partir del build.bat

4. Compila con Maven:
   bash
   mvn clean
   mvn compile
   
Esto generará las clases de ANTLR (ExprLexer, ExprParser, etc.) y compilará todo el código Java.


5. Empaqueta el codigo en un .jar
   bash
   mvn package


*Verifica tus versiones:*

bash
java -version
mvn -v

---


## Uso de la Cli Expressor

Desde la carpeta raiz del proyecto en una ventada de cmd:

Comando principal:
   bash
   expressor

Subcomandos:  
1. **transpile**
   ```
   expressor transpile {Direccion del archivo .expresso} --out {carpeta de salida (. para directorio actual)}
   ```
2. **build**
   ```
    expressor build {Direccion del archivo .java} --out {carpeta de salida}
   ```
3. **run**
   ```
    expressor run {Nombre del archivo.class} --out {Direccion en la que se encuentra}
   ```


## Uso del REPL

En la raiz del proyecto se encuentra un repl.bat el cual ejecuta el comando

bash
mvn exec:java

Y abre una terminal similar a un shell, donde se pueden escribir expresiones sin tener que declararlas como variables.
Nota: Expresiones como let, print, -> no se encuentran implementadas para usarse en esta consola.

Ejemplo de uso:

>>> Expresso REPL iniciado (escriba .exit para salir)

> 2+3*4
14

> -(10+4)/2
-7

> 3? 3*3/3 : (4*0+1)
3

> .exit


---

## Estructura del Proyecto
 ```
expresso/
├── build.bat                             # Agrega Maven en el caso que no este instalado
├── expresso.bat                          # Agrega un alias expressor al archivo expresso-1.0-Mediano.jar
├── mvnw/                                 #Archivos de Instalacion de Maven
│    └── ...
│
├── pom.xml
├── README.md
├── repl.bat                              # Abre una consola con el Repl. 
│
├── src/
│   └── main/
│       ├── antlr4/
│       │   └── expresso/
│       │       └── Expr.g4                # Gramática ANTLR4 principal
│       │
│       └── java/
│           └── expresso/
│               ├── ast/                   # Clases del Árbol de Sintaxis Abstracto (AST)
│               ├── cli/                   # Implementaciones de línea de comandos (CLI)
│               ├── parser/                # Clases generadas y adaptadores del parser
│               ├── runtime/               # Módulo de ejecución / intérprete
│               └── transpiler/            # Transpilador a código Java
│
├── target/
│   ├── expresso-1.0-Mediano.jar           # JAR ejecutable final
│   ├── classes/                           # Archivos compilados .class
│   ├── generated-sources/
│   │   └── antlr4/
│   │       ├── Expr.tokens
│   │       ├── ExprLexer.tokens
│   │       └── expresso/                  # Código generado por ANTLR
│   ├── maven-archiver/
│   ├── maven-status/
│   └── ...
│
└── test/                                 # Ejemplos de Implementacion de Expresso
    ├── HelloWorld0.expresso
    ├── HelloWorld1.expresso
    └── HelloWorld2.expresso
```


### Descripción de carpetas clave
- **pom.xml** → Configuración de dependencias y plugins de Maven.  
- **src/main/antlr4/expresso/Expr.g4** → Contiene las Expresiones Regulares de las cuales se va a modelar el AST.  
- **src/main/java/expresso/** → Código fuente principal, se encuentra dividido en 5 areas: ast/, cli/, parser/, runtime/ y transpiler/
- **test** → Destinada a almacenar archivos de prueba .expresso.  
- **target/** → Carpeta de salida de Antlr4 con las clases generadas en base a la gramática y de Maven con artefactos compilados (`.class`, `.jar`). 

---
## Referencias(Ordenadas en orden Alfabético):

- Apache Maven Project. (2023). Introducción al ciclo de vida de Maven. Recuperado de https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html

- Apache Software Foundation. Maven Compiler Plugin. Obtenido de https://maven.apache.org/plugins/maven-compiler-plugin

- Apache Software Foundation. Maven Shade Plugin. Obtenido de https://maven.apache.org/plugins/maven-shade-plugin

- ChatGPT (OpenAI). (27 de septiembre de 2025). “Adaptar proyecto Maven” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68d862f8-bc20-8000-b16d-94d0ef9cce4c

- ChatGPT (OpenAI). (27 de septiembre de 2025). “Migracion Codigo Expresso” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68d8b550-144c-8000-bdfc-285ff6a436f7

- ChatGPT (OpenAI). (28 de septiembre de 2025). “Boilerplate en Expressor.java” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68d8bf49-2da4-8000-b251-567f066ae6e0

- ChatGPT (OpenAI). (04 de octubre de 2025). “Condiciones operador ternario” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68e232d8-b4fc-8000-a764-a83294d31c9c

- ChatGPT (OpenAI). (05 de octubre de 2025). “Usar Maven sin instalacion” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68e2514d-73ec-8000-8453-968a1ab3e4c7

- Domino. K, Parr. T (2023). ANTLR4 Documentation. GitHub. Recuperado de https://github.com/antlr/antlr4/blob/master/doc/index.md

- Picocli. Documentación oficial. Recuperado de https://picocli.info

---

Proyecto académico migrado a Java usando *ANTLR4 + Maven*.  




