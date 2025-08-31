# EXPRESSO - UN MINILENGUAJE MUY CONCENTRADO
## SPRINT INICIAL

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
- SO: **Windows 10/11**
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

## GUIA RAPIDA PARA INSTALAR EXPRESSOR

**Compilar el `.jar` del proyecto**

En la raiz del proyecto, ejecuta:
```
mvn clean package
```
El cual generará el archivo `expresso-1.0-SNAPSHOT.jar` dentro de `target/`.

**Crear la imagen de la app con Jpackage**

Este paso difiere dependiedo si se hace desde cmd o desde terminal/powershell.

- CMD (Símbolo del sistema)
```
jpackage --type app-image --name expressor ^
  --input ".\target" ^
  --main-jar "expresso-1.0-SNAPSHOT.jar" ^
  --main-class "prdgms.Expressor" ^
  --app-version 1.0 ^
  --java-options "--enable-preview" ^
  --win-console ^
  --dest "."

mkdir ".\expressor\app\resources" 2>nul

xcopy ".\resources" ".\expressor\app\resources" /E /I /Y >nul

```
-PoweShell / Terminal
```
jpackage --type app-image `
  --name expressor `
  --input ".\target" `
  --main-jar "expresso-1.0-SNAPSHOT.jar" `
  --main-class "prdgms.Expressor" `
  --app-version 1.0 `
  --java-options "--enable-preview" `
  --win-console `
  --dest "."
New-Item -ItemType Directory -Force -Path ".\expressor\app\resources" | Out-Null
Copy-Item -Recurse -Force ".\resources\*" ".\expressor\app\resources\"

```
**"Instalar" moviendo la ruta al PATH**

Para que expressor se pueda ejecutar desde cualquier carpeta del sistema, se necesita agregarla a las variables de entorno de Windows.
- Mueve la carpeta expressor/ que generó el paso anterior a la ubicación que prefieras, por ejemplo `C:/Tools/expressor`.
- Agrega `C:/Tools/expressor` (Esta ruta contiene el ejecutable .exe) al PATH de usuario:
   1. Abre Editar las variables de entorno del sistema → Variables de entorno.
   2. En Variables de usuario, selecciona la opción llamada PATH → Editar → Nuevo.
   3. Escribe la ruta de expressor y guarda con Aceptar en todas las ventanas.

**Usar la herramienta**

Desde cmd, se puede ejecutar 
```
expressor --help
```
Para probar que expressor funciona correctamente.

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

## Referencias(Ordenadas en orden Alfabético):

- Apache Maven Project. (2023). Introducción al ciclo de vida de Maven. Recuperado de https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html

- Apache Software Foundation. Maven Compiler Plugin. Obtenido de https://maven.apache.org/plugins/maven-compiler-plugin

- Apache Software Foundation. Maven Shade Plugin. Obtenido de https://maven.apache.org/plugins/maven-shade-plugin

- ChatGPT (OpenAI). (29 de agosto de 2025). “El sistema no puede encontrar la ruta especificada” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68b3e03d-608c-8010-88eb-87de7a1f7904

- ChatGPT (OpenAI). (30 de agosto de 2025). “El problema es que --resource-dir resources no está pasando/copiando la carpeta resources correctamente al empaquetado, a pesar de que esta carpeta ya existe y tiene datos. ¿Sabes por qué ocurre esto?” [Conversa compartida]. ChatGPT. https://chatgpt.com/share/68b3e0d4-18e0-8000-b83a-0fbf2844ea1e

- Claude AI (Anthropic). (29 de agosto de 2025). “Crear un proyecto Maven en Java” [Conversa compartida]. Claude AI. https://claude.ai/share/45f7afb7-1f77-4e37-a1b8-97e175c9c561

- Oracle. (2023). jpackage: Empaquetado de aplicaciones Java. Documentación oficial de Java SE 21. Recuperado de https://docs.oracle.com/en/java/javase/21/jpackage

- Picocli. Documentación oficial. Recuperado de https://picocli.info


