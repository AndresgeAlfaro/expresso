package com.expresso.util;

import java.util.Scanner;

public class StdIO {
    private static final Scanner sc = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    public static void print(String msg) {
        System.out.println(msg);
    }
}
