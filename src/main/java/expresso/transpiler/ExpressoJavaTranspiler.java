package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import expresso.ast.Ast;
import expresso.parser.ConvertToAST;
import expresso.transpiler.JavaGenerator;

public class ExpressoJavaTranspiler {

    public static Path transpile(Path input, Path outputDir, boolean verbose) throws IOException {
        // 1. Leer el archivo .expresso
        String source = Files.readString(input);

        // 2. Convertir a AST
        Ast ast = ConvertToAST.fromString(source);

        // 3. Generar código Java desde el AST
        String javaCode = JavaGenerator.generate(ast);

        // 4. Asegurar carpeta de salida
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // Nombre del archivo .java (por ahora fijo "Main.java")
        Path javaFile = outputDir.resolve("Main.java");

        // 5. Guardar el archivo .java
        Files.writeString(javaFile, javaCode);

        if (verbose) {
            System.out.println("Generated Java code at: " + javaFile);
        }

        return javaFile;
    }
}