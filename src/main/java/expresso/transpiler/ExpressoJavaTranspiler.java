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
        
        // 2. Extraer nombre del archivo (sin extensión)
        String fileName = input.getFileName().toString();
        String className = extractClassName(fileName);
        
        // 3. Convertir a AST
        Ast ast = ConvertToAST.fromString(source);
        
        // 4. Generar código Java con el nombre de clase apropiado
        String javaCode = JavaGenerator.generate(ast, className);
        
        // 5. Asegurar carpeta de salida
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        
        // 6. Nombre del archivo .java (debe coincidir con el nombre de la clase)
        Path javaFile = outputDir.resolve(className + ".java");
        
        // 7. Guardar el archivo .java
        Files.writeString(javaFile, javaCode);
        
        if (verbose) {
            System.out.println("Generated Java code at: " + javaFile);
            System.out.println("Class name: " + className);
        }
        
        return javaFile;
    }
    
    // Extrae el nombre de clase del nombre de archivo
    private static String extractClassName(String fileName) {
        // Remover extensión
        if (fileName.endsWith(".expresso")) {
            fileName = fileName.substring(0, fileName.length() - 9);
        }
        
        // Convertir snake_case o kebab-case a PascalCase
        String[] parts = fileName.split("[_\\-]");
        StringBuilder className = new StringBuilder();
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                // Primera letra en mayúscula
                className.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    className.append(part.substring(1).toLowerCase());
                }
            }
        }
        
        String result = className.toString();
        
        // Si está vacío o es inválido, usar Main
        return (result.isEmpty() || !Character.isJavaIdentifierStart(result.charAt(0))) 
            ? "Main" 
            : result;
    }
}