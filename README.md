# EXPRESSO - UN MINILENGUAJE MUY CONCENTRADO
## SPRING INICIAL

*Versión:* 30/08/2025 v1.1 
*Curso:* EIF400 – Paradigmas de Programación (II-2025)

*Autores:*  
- Andrés Alfaro Ramírez - 119190404  
- Rafael Blanco Badilla - 119310876  
- Marquely Núñez Morales - 118830724  
- Randy Núñez Vargas - 119100297  

Grupo: 01-1pm

---

## DESCRIPCIÓN
Expresso es un mini-lenguaje diseñado para capturar lo esencial de la programación funcional (FP) mediante una sintaxis reducida pero expresiva.  
El objetivo de este proyecto es transpilar Expresso a Java 23+, manteniendo legibilidad y aprovechando características modernas del lenguaje.  

El entregable actual incluye una herramienta CLI **expressor** desarrollada en Java 23+, que permite simular las operaciones de:
1. **transpile** → Copiar un archivo template de Java.  
2. **build** → Transpilar y compilar el archivo Java en `.class`.  
3. **run** → Compilar y ejecutar la clase generada.  

---

## OBJETIVOS DEL PROYECTO
- Desarrollar un transpilador de Expresso a Java (jdk 23+).  
- Crear una herramienta CLI expressor que permita lo siguiente:  
  1. Transpilar (`transpile`)  
  2. Compilar (`build`)  
  3. Ejecutar (`run`)  
- Implementar un parser en **ANTLR4** y un **minityper** para validaciones semánticas.  
- Proveer una batería de casos de prueba para cada sprint.  
- Garantizar un proyecto colaborativo, verificable y escalable.  

---

## REQUISITOS TÉCNICOS
- Lenguaje: **Java 23+**  
- Parser: **ANTLR4**  
- Gestión de dependencias: **Maven o Gradle** (se puede compilar sin IDE) (En este caso se usa Maven) 
- **Unit Testing**: obligatorio  
- CLI: basado en **Picocli**  
- Compilación dinámica: `javax.tools.JavaCompiler`  

---

## USO DE LA CLI EXPRESSOR
Comando principal: 
```
expressor
```

Subcomandos:  
1. **transpile**
   ```
   expressor transpile --out output HelloWorld.expresso
   ```
2. **build**
   ```
    expressor build --out output HelloWorld.expresso
   ```
3. **run**
   ```
    expressor run --out output HelloWorld.expresso
   ```
**HelloWorld se puede reemplazar por el nombre del archivo por ejecutar.

Compilación y ejecución general:
```
mvn clean package
```

---

## ESTRUCTURA DEL PROYECTO

El proyecto **Expresso** sigue una organización estándar de Maven, lo cual facilita la compilación, empaquetado y ejecución desde la línea de comandos o IDEs.  

```
expresso/
│
├── pom.xml                          # Archivo de configuración de Maven
├── dependency-reduced-pom.xml       # POM generado al hacer shading/assembly
├── README.md                        # Documentación inicial del proyecto
│
├── resources/                       # Recursos adicionales
│   └── template/
│       └── HelloWorld.java          # Plantilla base utilizada en 'transpile'
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── prdgms/
│   │           └── Expressor.java   # Clase principal que implementa la CLI
│   │
│   └── test/
│       └── java/
│           └── prdgms/              # Espacio reservado para pruebas unitarias
│
├── target/                          # Carpeta generada por Maven
│   ├── expresso-1.0-SNAPSHOT.jar    # JAR ejecutable principal
│   ├── original-expresso-1.0-SNAPSHOT.jar
│   ├── classes/                     # Clases compiladas
│   │   └── prdgms/
│   │       ├── Expressor.class
│   │       ├── Expressor$Transpile.class
│   │       ├── Expressor$Build.class
│   │       └── Expressor$Run.class
│   ├── test-classes/                # Clases de pruebas compiladas
│   ├── generated-sources/           # Fuentes generadas automáticamente
│   ├── generated-test-sources/
│   ├── maven-archiver/
│   │   └── pom.properties
│   └── maven-status/                # Metadatos de compilación
```

### Descripción de carpetas clave
- **pom.xml** → Configuración de dependencias y plugins de Maven.  
- **resources/template/** → Contiene las plantillas Java utilizadas por el comando `transpile`.  
- **src/main/java/prdgms/** → Código fuente principal, incluyendo la clase `Expressor` con la lógica de la CLI.  
- **src/test/java/prdgms/** → Destinado a pruebas unitarias (a completar en futuros sprints).  
- **target/** → Carpeta de salida de Maven con artefactos compilados (`.class`, `.jar`) y metadatos de construcción.  

