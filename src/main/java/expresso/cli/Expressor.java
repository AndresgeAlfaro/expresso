package expresso.cli;

/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
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

import java.nio.file.Path;
import java.util.concurrent.Callable;

import expresso.transpiler.ExpressoJavaTranspiler;
import expresso.transpiler.JavaCompilerService;
import expresso.transpiler.TemplateLocator;
import expresso.cli.ExpressorUtils;

@Command(
    name = "expressor",
    mixinStandardHelpOptions = true,
    description = "CLI for Expresso",
    subcommands = {
        Expressor.Transpile.class,
        Expressor.Build.class,
        Expressor.Run.class
    }
)
public class Expressor implements Callable<Integer> {
    
    public static void main(String[] args){
        int exit = new CommandLine(new Expressor()).execute(args);
        System.exit(exit);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    // ======================= Expressor Commands: =========================== 


    @Command(name = "transpile", description = "Transpile Expresso source into Java code")
    static class Transpile implements Callable<Integer> {
        @Option(names = "--out", description = "Output folder")
        private String out;

        @Option(names = "--verbose", description = "Enable verbose output")
        private boolean verbose;

        @Parameters(index = "0", description = ".expresso file to transpile")
        private String expFile;

        @Override
        public Integer call() {
            try {
                Path input = ExpressorUtils.validateExpressoFile(expFile);
                Path output = ExpressorUtils.resolveOut(out);

                ExpressoJavaTranspiler.transpile(input, output, verbose);

                if (verbose) {
                    System.out.println("Transpilation complete: " + input.getFileName() + " -> " + output);
                }
                return 0;
            } catch (Exception e) {
                System.err.println("ERROR (transpile): " + e.getMessage());
                return 1;
            }
        }
    }
    @Command(name = "build", description = "Compile the generated .java files into .class files")
    static class Build implements Callable<Integer> {
        @Option(names = "--out", description = "Output folder")
        private String out;

        @Option(names = "--verbose", description = "Enable verbose output")
        private boolean verbose;

        @Parameters(index = "0", description = ".java file to compile")
        private String javaFile;

        @Override
        public Integer call() {
            try {
                Path input = Path.of(javaFile).toAbsolutePath().normalize();
                Path output = ExpressorUtils.resolveOut(out);

                JavaCompilerService.compileJava(input, output);

                if (verbose) {
                    System.out.println(" Build complete: " + input.getFileName() + " → " + output);
                }
                return 0;
            } catch (Exception e) {
                System.err.println("ERROR (build): " + e.getMessage());
                return 1;
            }
        }
    }

    // -------------------- Subcommand: run --------------------
    @Command(name = "run", description = "Execute a compiled .class program")
    static class Run implements Callable<Integer> {
        @Option(names = "--out", description = "Output folder (compiled classes)")
        private String out;

        @Option(names = "--verbose", description = "Enable verbose output")
        private boolean verbose;

        @Parameters(index = "0", description = "Main class to execute (without .class)")
        private String mainClass;

        @Override
        public Integer call() {
            try {
                Path output = ExpressorUtils.resolveOut(out);

                JavaCompilerService.runClass(mainClass, output);

                if (verbose) {
                    System.out.println(" Run finished successfully.");
                }
                return 0;
            } catch (Exception e) {
                System.err.println("ERROR (run): " + e.getMessage());
                return 1;
            }
        }
    }
}   



 