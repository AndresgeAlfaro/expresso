package expresso.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExpressorUtils {
    
    // Validates that the file is .Expresso or .expresso and returns its Path
    public static Path validateExpressoFile(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Missing .expresso file");
        }
        Path p = Paths.get(filePath);
        if (!Files.exists(p) || !Files.isRegularFile(p)) {
            throw new IOException("File not found: " + filePath);
        }
        String name = p.getFileName().toString();
        if (!(name.endsWith(".expresso") || name.endsWith(".Expresso"))) {
            throw new IllegalArgumentException("File must end with .expresso or .Expresso: " + name);
        }
        return p.toAbsolutePath().normalize();
    }

    
    // Para despues agregar:
    //Considerar que este metodo puede o podria convertir el expreso a Mayus
    //Quitar caracteres no validos
    //Concatenar la carpeta de salida
    //Lo de: Los nombres de fuentes Java generados deben empezar con una mayúscula y no tener caracteres alfabéticos. Si el nombre del archivo .expresso no empieza en mayúscula se le cambia para que así sea.
    public static Path resolveOut(String path) {
        return (path == null || path.trim().isEmpty())
            ? Paths.get(".").toAbsolutePath().normalize()
            : Paths.get(path).toAbsolutePath().normalize();
    }









}
