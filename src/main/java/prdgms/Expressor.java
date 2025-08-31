package prdgms;

/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Inicial)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject;

import java.nio.file.*;
import java.util.concurrent.Callable;
import java.util.Locale;
import java.util.*;
import java.io.*;

@Command(name = "expressor", mixinStandardHelpOptions = true, description = "CLI para Expresso")
public class Expressor implements Callable<Integer> {
    public static void main(String[] args) {
        int exit = new CommandLine(new Expressor())
                .addSubcommand("transpile", new Transpile())
                .addSubcommand("build", new Build())
                .addSubcommand("run", new Run())
                .execute(args);
        System.exit(exit);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    /* =========================
       Helpers de paths y runtime
       ========================= */

    // --out por defecto => "."
    static Path resolveOut(String out) {
        return (out == null || out.trim().isEmpty())
                ? Paths.get(".").toAbsolutePath().normalize()
                : Paths.get(out).toAbsolutePath().normalize();
    }

    // Busca resources/template/HelloWorld.java
    // 1) relativo al CWD (raíz del proyecto, como pide el enunciado)
    // 2) empaquetado con jpackage (--resource-dir resources) bajo .../app/resources/template
    // 3) junto al binario/jar
    static Path findTemplatePath(boolean verbose) {
        // 1) CWD
        Path cwdTemplate = Paths.get("resources", "template", "HelloWorld.java");
        if (Files.isRegularFile(cwdTemplate)) {
            if (verbose) System.out.println("Template (CWD): " + cwdTemplate.toAbsolutePath());
            return cwdTemplate.toAbsolutePath();
        }

        // 2) Imagen jpackage
        String launcher = System.getProperty("jpackage.app-path"); // .../expressor.exe
        if (launcher != null) {
            Path appDir = Paths.get(launcher).getParent().resolve("app");
            Path packed = appDir.resolve("resources").resolve("template").resolve("HelloWorld.java");
            if (Files.isRegularFile(packed)) {
                if (verbose) System.out.println("Template (jpackage): " + packed.toAbsolutePath());
                return packed.toAbsolutePath();
            }
        }

        // 3) Cerca del artefacto
        try {
            Path self = Paths.get(Expressor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = self.getParent(); // target/ ó carpeta del exe
            Path near = base.resolve("resources").resolve("template").resolve("HelloWorld.java");
            if (Files.isRegularFile(near)) {
                if (verbose) System.out.println("Template (junto al binario): " + near.toAbsolutePath());
                return near.toAbsolutePath();
            }
        } catch (Exception ignore) { }

        return null;
    }

    // Valida archivo .Expresso/.expresso (existe y no vacío)
    static Path validateExpressoFile(String fileArg) throws IOException {
        if (fileArg == null) throw new IllegalArgumentException("Falta el archivo .Expresso/.expresso");
        Path p = Paths.get(fileArg);
        if (!Files.isRegularFile(p)) {
            throw new FileNotFoundException("No existe el archivo: " + p);
        }
        if (Files.size(p) == 0) {
            throw new IOException("El archivo está vacío: " + p);
        }
        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.endsWith(".expresso")) {
            throw new IllegalArgumentException("El archivo debe terminar en .Expresso/.expresso: " + p.getFileName());
        }
        return p.toAbsolutePath().normalize();
    }

    // Copia el template al directorio de salida
    static int copyTemplateToOutput(Path template, Path outDir, boolean verbose) throws IOException {
        if (template == null || !Files.exists(template)) {
            System.err.println("ERROR: No se encontró resources/template/HelloWorld.java");
            return 1;
        }
        if (Files.size(template) == 0) {
            System.err.println("ERROR: template está vacío: " + template);
            return 1;
        }
        if (verbose) System.out.println("Creando carpeta de salida: " + outDir);
        Files.createDirectories(outDir);
        Path target = outDir.resolve("HelloWorld.java");
        Files.copy(template, target, StandardCopyOption.REPLACE_EXISTING);
        if (verbose) System.out.println("Copiado template -> " + target);
        return 0;
    }

    // Localiza binarios java/javac del runtime activo
    static Path javaBin(String tool) {
        // tool = "java" | "javac"
        return Paths.get(System.getProperty("java.home"), "bin", tool + (isWindows() ? ".exe" : ""));
    }

    static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("win");
    }

    // Ejecuta un proceso heredando consola; retorna exit code
    static int execInherit(List<String> cmd, boolean verbose) throws IOException, InterruptedException {
        if (verbose) System.out.println("Ejecutando: " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        return p.waitFor();
    }

    // Compilar HelloWorld.java -> *.class en outDir
    static boolean compileJava(Path javaFile, Path outDir, boolean verbose) throws IOException {
        // 1) Intentar con javac del runtime activo
        Path javac = javaBin("javac");
        if (Files.isRegularFile(javac)) {
            int ec;
            try {
                ec = execInherit(Arrays.asList(
                        javac.toString(), "-d", outDir.toString(), javaFile.toString()
                ), verbose);
                if (ec == 0) {
                    if (verbose) System.out.println("Compilación exitosa (javac).");
                    return true;
                }
                System.err.println("ERROR: compilación fallida (javac) con código " + ec);
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Compilación interrumpida", e);
            }
        }

        // 2) Fallback: ToolProvider (requiere JDK, no funciona con JRE/jlinked)
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            if (verbose) System.out.println("Compilando con ToolProvider (embebido)...");
            try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null)) {
                Iterable<? extends JavaFileObject> units = fm.getJavaFileObjects(javaFile.toFile());
                List<String> options = Arrays.asList("-d", outDir.toString());
                JavaCompiler.CompilationTask task = compiler.getTask(null, fm, null, options, null, units);
                Boolean ok = task.call();
                if (ok != null && ok) {
                    if (verbose) System.out.println("Compilación exitosa (ToolProvider).");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("ERROR: compilación fallida (ToolProvider): " + e.getMessage());
                return false;
            }
        }

        // 3) Sin javac y sin ToolProvider
        System.err.println("ERROR: No se encontró 'javac' ni JavaCompiler embebido.");
        System.err.println("       Ejecute con un JDK, o compile desde el JAR en un entorno con JDK,");
        System.err.println("       o empaquete la imagen con un JDK que incluya herramientas de compilación.");
        return false;
    }

    /* ==============
       Subcomandos CLI
       ============== */

    @Command(name = "transpile", description = "Transpila: copia template a --out")
    static class Transpile implements Callable<Integer> {
        @Option(names = "--out", description = "Carpeta de salida (por defecto .)")
        private String out;

        @Option(names = "--verbose", description = "Mostrar pasos")
        private boolean verbose = false;

        @Parameters(index = "0", description = "Archivo .Expresso/.expresso a procesar")
        private String expFile;

        @Override
        public Integer call() throws Exception {
            try {
                Path in = validateExpressoFile(expFile);
                if (verbose) System.out.println("Leyendo: " + in);
                Path template = findTemplatePath(verbose);
                Path outDir = resolveOut(out);
                return copyTemplateToOutput(template, outDir, verbose);
            } catch (Exception e) {
                System.err.println("ERROR (transpile): " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "build", description = "Copia el template y compila .java -> .class en --out")
    static class Build implements Callable<Integer> {
        @Option(names = "--out", description = "Carpeta de salida (por defecto .)")
        private String out;

        @Option(names = "--verbose", description = "Mostrar pasos")
        private boolean verbose = false;

        @Parameters(index = "0", description = "Archivo .Expresso/.expresso a procesar")
        private String expFile;

        @Override
        public Integer call() throws Exception {
            try {
                Path in = validateExpressoFile(expFile);
                if (verbose) System.out.println("Leyendo: " + in);

                Path template = findTemplatePath(verbose);
                Path outDir = resolveOut(out);

                int r = copyTemplateToOutput(template, outDir, verbose);
                if (r != 0) return r;

                Path javaFile = outDir.resolve("HelloWorld.java");
                boolean ok = compileJava(javaFile, outDir, verbose);
                return ok ? 0 : 2;
            } catch (Exception e) {
                System.err.println("ERROR (build): " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "run", description = "Compila (si hace falta) y ejecuta la clase generada")
    static class Run implements Callable<Integer> {
        @Option(names = "--out", description = "Carpeta de salida (por defecto .)")
        private String out;

        @Option(names = "--verbose", description = "Mostrar pasos")
        private boolean verbose = false;

        @Parameters(index = "0", description = "Archivo .Expresso/.expresso a procesar")
        private String expFile;

        @Override
        public Integer call() throws Exception {
            // Primero build
            Build b = new Build();
            b.out = this.out;
            b.verbose = this.verbose;
            b.expFile = this.expFile;
            Integer code = b.call();
            if (code != 0) return code;

            Path outDir = resolveOut(out);
            Path classFile = outDir.resolve("HelloWorld.class");
            if (!Files.isRegularFile(classFile)) {
                System.err.println("ERROR: no se encontró HelloWorld.class en " + outDir);
                return 3;
            }
            if (verbose) System.out.println("Ejecutando la clase HelloWorld...");

            // Ejecutar con el java del runtime activo si existe; si no, usar "java" del PATH
            Path java = javaBin("java");
            List<String> cmd = Files.isRegularFile(java)
                    ? Arrays.asList(java.toString(), "-cp", outDir.toString(), "HelloWorld")
                    : Arrays.asList("java", "-cp", outDir.toString(), "HelloWorld");
            try {
                int exit = execInherit(cmd, verbose);
                if (verbose) System.out.println("Proceso terminado con código: " + exit);
                return exit;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("ERROR: ejecución interrumpida");
                return 4;
            }
        }
    }
}
