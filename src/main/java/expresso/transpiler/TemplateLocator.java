package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//Nota: Estas clases eran para copiar la direccion del HelloWorld.java en resources
// Si no estan siendo usadas al final del desarrollo, las podemos eliminar
// y quitar esta clase de transpiler
public class TemplateLocator{


    // Looks up a test file in the "test" directory.
    public static Path findTemplatePath(String fileName) {
        Path path = Paths.get("test", fileName);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Template not found: " + fileName);
        }
        return path.toAbsolutePath();
    }

        // Copia el template hacia la carpeta de salida
    public static void copyTemplateToOutput(Path inputPath, Path outDir) throws IOException {
        if (inputPath == null || !Files.exists(inputPath)) {
            throw new IllegalArgumentException("Source file does not exist: " + inputPath);
        }
        if (Files.size(inputPath) == 0) {
            throw new IllegalArgumentException("Source file is empty: " + inputPath);
        }

        Files.createDirectories(outDir);
        Path target = outDir.resolve(inputPath.getFileName());
        Files.copy(inputPath, target, StandardCopyOption.REPLACE_EXISTING);
    }


}