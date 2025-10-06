
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import expresso.ast.Ast;
import expresso.parser.ConvertToAST;

public class ExpressoJavaTranspiler {

    // ==== MAIN TRANSPILER ====
    public static Path transpile(Path input, Path outputDir, boolean verbose) throws IOException {
        // 1. Read .expresso file
        var source = Files.readString(input);

        // 2. Derive class name from file name
        var className = extractClassName(input.getFileName().toString());

        // 3. Parse to AST
        Ast ast = ConvertToAST.fromString(source);

        // 4. Generate Java code
        var javaCode = JavaGenerator.generate(ast, className);

        // 5. Ensure output directory exists
        Optional.of(outputDir)
                .filter(dir -> Files.notExists(dir))
                .ifPresent(dir -> {
                    try { Files.createDirectories(dir); }
                    catch (IOException e) { throw new RuntimeException(e); }
                });

        // 6. Build Java file path
        var javaFile = outputDir.resolve(className + ".java");

        // 7. Write Java code to file
        Files.writeString(javaFile, javaCode);

        // 8. Verbose output (optional)
        Optional.of(verbose)
                .filter(v -> v)
                .ifPresent(v -> {
                    System.out.println("Generated Java code at: " + javaFile);
                    System.out.println("Class name: " + className);
                });

        return javaFile;
    }

    // ==== CLASS NAME EXTRACTION ====
    private static String extractClassName(String fileName) {
        // Remove ".expresso" extension if present
        var name = Optional.ofNullable(fileName)
                .map(n -> n.endsWith(".expresso") ? n.substring(0, n.length() - 9) : n)
                .orElse("Main");

        // Convert snake_case or kebab-case to PascalCase
        var result = Stream.of(name.split("[_\\-]"))
                .filter(s -> !s.isEmpty())
                .map(s -> Character.toUpperCase(s.charAt(0)) +
                          (s.length() > 1 ? s.substring(1).toLowerCase() : ""))
                .collect(Collectors.joining());

        // Return "Main" if invalid or empty
        return Optional.of(result)
                .filter(r -> !r.isEmpty() && Character.isJavaIdentifierStart(r.charAt(0)))
                .orElse("Main");
    }
}
