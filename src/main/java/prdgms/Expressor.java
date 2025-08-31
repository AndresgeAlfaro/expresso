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
import java.util.*;
import java.io.*;

@Command(name = "expressor", mixinStandardHelpOptions = true, description = "CLI for Expresso")
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

    /* Path Helper */

    
    static Path resolveOut(String out) {
        return (out == null || out.trim().isEmpty())
                ? Paths.get(".").toAbsolutePath().normalize() //  "." means by default when using --out
                : Paths.get(out).toAbsolutePath().normalize();
    }

    //This method looks up HelloWorld.java, necessary for packaging with jpackage and generating the .jar file.
    static Path findTemplatePath(boolean verbose) {
        Path cwdTemplate = Paths.get("resources", "template", "HelloWorld.java");
        if (Files.isRegularFile(cwdTemplate)) {
            if (verbose) System.out.println("Template (CWD): " + cwdTemplate.toAbsolutePath());
            return cwdTemplate.toAbsolutePath();
        }

        String launcher = System.getProperty("jpackage.app-path"); 
        if (launcher != null) {
            Path appDir = Paths.get(launcher).getParent().resolve("app");
            Path packed = appDir.resolve("resources").resolve("template").resolve("HelloWorld.java");
            if (Files.isRegularFile(packed)) {
                if (verbose) System.out.println("Template (jpackage): " + packed.toAbsolutePath());
                return packed.toAbsolutePath();
            }
        }

        try {
            Path self = Paths.get(Expressor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = self.getParent(); 
            Path near = base.resolve("resources").resolve("template").resolve("HelloWorld.java");
            if (Files.isRegularFile(near)) {
                if (verbose) System.out.println("Template (next to the binary file): " + near.toAbsolutePath()); 
                return near.toAbsolutePath();
            }
        } catch (Exception ignore) { }

        return null;
    }

    // Validates that the file is .Expresso or .expresso.
    static Path validateExpressoFile(String fileArg) throws IOException {
        if (fileArg == null) throw new IllegalArgumentException("Missing file .Expresso/.expresso");
        Path p = Paths.get(fileArg);
        if (!Files.isRegularFile(p)) {
            throw new FileNotFoundException("This file doesn't exist: " + p);
        }
        if (Files.size(p) == 0) {
            throw new IOException("This file is empty: " + p);
        }
        String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.endsWith(".expresso")) {
            throw new IllegalArgumentException("The file must end in .Expresso/.expresso: " + p.getFileName());
        }
        return p.toAbsolutePath().normalize();
    }

    
    static int copyTemplateToOutput(Path template, Path outDir, boolean verbose) throws IOException {
        if (template == null || !Files.exists(template)) {
            System.err.println("ERROR: Cannot find resources/template/HelloWorld.java");
            return 1;
        }
        if (Files.size(template) == 0) {
            System.err.println("ERROR: template is empty: " + template);
            return 1;
        }
        if (verbose) System.out.println("Creating output folder: " + outDir);  
        Files.createDirectories(outDir);
        Path target = outDir.resolve("HelloWorld.java");
        Files.copy(template, target, StandardCopyOption.REPLACE_EXISTING);
        if (verbose) System.out.println("Copying template -> " + target);
        return 0;
    }

    // Searches up the binaries files for java and javac on the active runtime
    static Path javaBin(String tool) {
        
        return Paths.get(System.getProperty("java.home"), "bin", tool + (isWindows() ? ".exe" : ""));
    }
    //Also verifies that the program is working on MS Windows.
    static boolean isWindows() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("win");
    }

    // Runs a process inheriting the console. Also returns the exit code
    static int execInherit(List<String> cmd, boolean verbose) throws IOException, InterruptedException {
        if (verbose) System.out.println("Executing: " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        Process p = pb.start();
        return p.waitFor();
    }

    // Compiles HelloWorld.java into .class files
    static boolean compileJava(Path javaFile, Path outDir, boolean verbose) throws IOException {

        Path javac = javaBin("javac");
        if (Files.isRegularFile(javac)) {
            int ec;
            try {
                ec = execInherit(Arrays.asList(
                        javac.toString(), "-d", outDir.toString(), javaFile.toString()
                ), verbose);
                if (ec == 0) {
                    if (verbose) System.out.println("Compilation succeeded (javac).");
                    return true;
                }
                System.err.println("ERROR: Failed Compilation (javac) " + ec);
                return false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Compilation interrupted", e);
            }
        }

        // 2) Fallback: ToolProvider (JDK)
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            if (verbose) System.out.println("Compiling with ToolProvider ");
            try (StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null)) {
                Iterable<? extends JavaFileObject> units = fm.getJavaFileObjects(javaFile.toFile());
                List<String> options = Arrays.asList("-d", outDir.toString());
                JavaCompiler.CompilationTask task = compiler.getTask(null, fm, null, options, null, units);
                Boolean ok = task.call();
                if (ok != null && ok) {
                    if (verbose) System.out.println("Compilation succeeded (ToolProvider).");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed Compilation (ToolProvider): " + e.getMessage());
                return false;
            }
        }

        // Generic Error Fallback
        System.err.println("ERROR: Couldn't find javac or Embedded Compiler.");
        return false;
    }

    /*     CLI     */

    @Command(name = "transpile", description = "Transpile: template to --out")
    static class Transpile implements Callable<Integer> {
        @Option(names = "--out", description = "Output folder")
        private String out;

        @Option(names = "--verbose", description = "Print to console")
        private boolean verbose = false;

        @Parameters(index = "0", description = " .Expresso/.expresso file")
        private String expFile;

        @Override
        public Integer call() throws Exception {
            try {
                Path in = validateExpressoFile(expFile);
                if (verbose) System.out.println("Reading: " + in);
                Path template = findTemplatePath(verbose);
                Path outDir = resolveOut(out);
                return copyTemplateToOutput(template, outDir, verbose);
            } catch (Exception e) {
                System.err.println("ERROR (transpile): " + e.getMessage());
                return 1;
            }
        }
    }

    @Command(name = "build", description = "Takes the template and compiles .java into .class files")
    static class Build implements Callable<Integer> {
        @Option(names = "--out", description = "Output folder")
        private String out;

        @Option(names = "--verbose", description = "Print to console")
        private boolean verbose = false;

        @Parameters(index = "0", description = " .Expresso/.expresso file")
        private String expFile;

        @Override
        public Integer call() throws Exception {
            try {
                Path in = validateExpressoFile(expFile);
                if (verbose) System.out.println("Reading: " + in);

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

    @Command(name = "run", description = "Compiles (if necessary) and executes the .class file")
    static class Run implements Callable<Integer> {
        @Option(names = "--out", description = "Output Folder")
        private String out;

        @Option(names = "--verbose", description = "Print to console")
        private boolean verbose = false;

        @Parameters(index = "0", description = ".Expresso/.expresso file")
        private String expFile;

        @Override
        public Integer call() throws Exception {

            Build b = new Build();
            b.out = this.out;
            b.verbose = this.verbose;
            b.expFile = this.expFile;
            Integer code = b.call();
            if (code != 0) return code;

            Path outDir = resolveOut(out);
            Path classFile = outDir.resolve("HelloWorld.class");
            if (!Files.isRegularFile(classFile)) {
                System.err.println("ERROR: couldn't find HelloWorld.class on " + outDir);
                return 3;
            }
            if (verbose) System.out.println("Executes HelloWorld class.");

            Path java = javaBin("java");
            List<String> cmd = Files.isRegularFile(java)
                    ? Arrays.asList(java.toString(), "-cp", outDir.toString(), "HelloWorld")
                    : Arrays.asList("java", "-cp", outDir.toString(), "HelloWorld");
            try {
                int exit = execInherit(cmd, verbose);
                if (verbose) System.out.println("Process completed with exit code: " + exit); 
                return exit;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("ERROR: interrupted execution.");
                return 4;
            }
        }
    }
}
