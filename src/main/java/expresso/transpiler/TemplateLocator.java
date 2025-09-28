package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TemplateLocator{


    // Looks up a test file in the "test" directory.
    public static Path findTemplatePath(String fileName){
        Path path = Paths.get("test", fileName);
        if(!Files.exists(path)){
            throw new IllegalArgumentException("Test not found: " + fileName);
        }
           
        return path;
    }

        // Copia el template hacia la carpeta de salida
    public static void copyTemplateToOutput(Path inputPath, Path outputPath) throws IOException {
        if ( inputPath == null || !Files.exists(inputPath)) {
            throw new IllegalArgumentException("Source file does not exist: " + inputPath);
        }
        if (Files.size(inputPath) == 0) {
            throw new IllegalArgumentException("Source file is empty: " + inputPath);
        }
        
        Path dir = outputPath.getParent();
        if(dir != null) {Files.createDirectories(dir);}

        Files.copy(inputPath,outputPath, StandardCopyOption.REPLACE_EXISTING);
    }


}