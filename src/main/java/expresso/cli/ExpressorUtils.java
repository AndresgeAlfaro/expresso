package expresso.cli;

import java.io.File;

public class ExpressorUtils {
    
    // Validates that the file is .Expresso or .expresso.
    public static void validateExpressoFile(String Path){
        File file = new File(Path);

        if(!file.exists() || !file.isFile() ||
        !file.getName().endsWith(".expresso")||
        !file.getName().endsWith(".Expresso")){

            throw new IllegalArgumentException("Invalid File: "+ Path);
        }

    }

    
    // Para despues
    //Considerar que este metodo puede o podria convertir el expreso a Mayus
    //Quitar caracteres no validos
    //Concatenar la carpeta de salida
    //Lo de: Los nombres de fuentes Java generados deben empezar con una mayúscula y no tener caracteres alfabéticos. Si el nombre del archivo .expresso no empieza en mayúscula se le cambia para que así sea.
    public static String resolveOut(String filePath, String outOption){
        if(outOption != null){
            return outOption;
        }
        
        return new File(filePath).getName().replace(".expresso", "") + ".java";
    }









}
