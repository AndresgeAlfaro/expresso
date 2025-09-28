package expresso.cli;

import java.util.Scanner;
import expresso.runtime.ParseEvaluate;

public class Repl {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println(">>> Expresso REPL iniciado (escriba .exit para salir)");
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine();
            if (line.equals(".exit")) break;
            try {
                int result = ParseEvaluate.run(line);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}
