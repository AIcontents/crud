package com.example.crudapp;

/**
 * A wrapper class to launch the JavaFX application.
 * This is a workaround for the "JavaFX runtime components are missing" error
 * when running a shaded JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
