package com.iitbase.common;

public class MemoryLogger {

    public static void log(String tag) {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long total = runtime.totalMemory() / (1024 * 1024);

        System.out.println("MEMORY [" + tag + "] Used: " + used + " MB / Total: " + total + " MB");
    }
}
