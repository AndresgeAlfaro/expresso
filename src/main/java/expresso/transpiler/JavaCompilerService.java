package expresso.transpiler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaCompilerService {

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static String javaBin(){
        if (!isWindows()) {
        System.err.println("WARNING: This program is only supported on MS Windows. Unexpected behaviour may happen.");
        }

        String javaHome = System.getProperty("java.home");
        
        return Paths.get(javaHome, "bin", isWindows() ? "javac.exe" : "javac").toString();
    }

    private static String runtimeBin(){
        String javaHome = System.getProperty("java.home");
        return Paths.get(javaHome, "bin", isWindows() ? "java.exe" : "java").toString();
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

    //called by run
    public static void runClass(String mainClass, Path classpath)
            throws IOException, InterruptedException {

        List<String> command = new ArrayList<>();
        command.add(runtimeBin());
        command.add("-cp");
        command.add(classpath.toString());
        command.add(mainClass);

        exec(command);
    }
 
    // execute
    private static void exec(List<String> command)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO(); 
        //pb.start can throw IOException
        Process process = pb.start();
        //.waitfor() can throw InterruptedException
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Process failed with exit code " + exitCode + ": " + command);
        }
    }

}