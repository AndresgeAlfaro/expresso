package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//import java.io.*;
//import java.nio.file.*;
//import java.util.*;

public class JavaCompilerService {

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static String javaBin(){
        if (!isWindows()) {
        System.err.println("WARNING: This program is only supported on MS Windows. Unexpected behaviour may happen.");
        }

        String javaHome = System.getProperty("java.home");
        Path javaPath = Paths.get(javaHome, "bin", isWindows() ? "javac.exe" : "javac");
        return javaPath.toString();
    }

    public static void compileJava(Path javaInput, Path outputDir)
                        throws IOException, InterruptedException{

        List<String> command = new ArrayList<>();
        command.add(javaBin());
        command.add("-d");
        command.add(outputDir.toString());
        command.add(javaInput.toString());

        ProcessBuilder pb = new ProcessBuilder(command);
        
        //pb.start can throw IOException
        Process process = pb.start();
        //.waitfor() can throw InterruptedException
        int exitCode = process.waitFor();
        
        if(exitCode != 0){
            throw new IOException("javac failed with exit code " + exitCode 
                          + " while compiling " + javaInput);
        }

    }   
}