
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class TemplateLocator {

    // ==== FIND TEMPLATE FILE ====
    public static Path findTemplatePath(String fileName) {
        return Optional.ofNullable(fileName)
                .map(name -> Paths.get("test", name))
                .filter(Files::exists)
                .map(Path::toAbsolutePath)
                .orElseThrow(() ->
                        new IllegalArgumentException("Template not found: " + fileName));
    }

    // ==== COPY TEMPLATE TO OUTPUT ====
    public static void copyTemplateToOutput(Path inputPath, Path outDir) throws IOException {
        Optional.ofNullable(inputPath)
                .filter(Files::exists)
                .filter(path -> {
                    try { return Files.size(path) > 0; }
                    catch (IOException e) { return false; }
                })
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid or empty source file: " + inputPath));

        Files.createDirectories(outDir);
        Files.copy(
                inputPath,
                outDir.resolve(inputPath.getFileName()),
                StandardCopyOption.REPLACE_EXISTING
        );
    }
}
