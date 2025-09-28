package expresso.runtime;

import java.io.IOException;

public class ProcessExecutor {
    public static void runCommand(String... command)
            throws IOException, InterruptedException{

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        //pb.start can throw IOException
        Process process = pb.start();
        //.waitfor() can throw InterruptedException
        int exitCode = process.waitFor();
        if(exitCode != 0){
             throw new IOException("process failed with exit code " + exitCode 
                          + " while executing " + command);
        }
    }
}
