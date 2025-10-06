
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

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
                int result = ParseEvaluate.runExpression(line);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}
