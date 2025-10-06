
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class JavaCompilerService {

    // ==== PLATFORM HELPERS ====
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static String javaBin() {
        var javaHome = System.getProperty("java.home");
        var exe = isWindows() ? "javac.exe" : "javac";

        Optional.of(isWindows())
                .filter(win -> !win)
                .ifPresent(x -> System.err.println(
                        "WARNING: This program is designed for Windows. Unexpected behavior may occur."
                ));

        return Paths.get(javaHome, "bin", exe).toString();
    }

    private static String runtimeBin() {
        var javaHome = System.getProperty("java.home");
        var exe = isWindows() ? "java.exe" : "java";
        return Paths.get(javaHome, "bin", exe).toString();
    }

    // ==== COMPILATION ====
    public static void compileJava(Path javaInput, Path outputDir)
            throws IOException, InterruptedException {

        var command = List.of(
                javaBin(),
                "-d", outputDir.toString(),
                javaInput.toString()
        );

        var process = new ProcessBuilder(command)
                .inheritIO()
                .start();

        Optional.of(process.waitFor())
                .filter(code -> code == 0)
                .orElseThrow(() -> new IOException(
                        "javac failed while compiling " + javaInput
                ));
    }

    // ==== EXECUTION ====
    public static void runClass(String className, Path classDir)
            throws IOException, InterruptedException {

        var command = List.of(
                runtimeBin(),
                "-cp", classDir.toString(),
                className
        );

        var process = new ProcessBuilder(command)
                .inheritIO()
                .start();

        Optional.of(process.waitFor())
                .filter(code -> code == 0)
                .orElseThrow(() -> new RuntimeException(
                        "Execution failed for class: " + className
                ));
    }
}
