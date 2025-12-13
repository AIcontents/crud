package com.example.crudapp.controller;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;

public abstract class JavaFxTestBase {

    @BeforeAll
    public static void initToolkit() throws InterruptedException {
        // Prevents "IllegalStateException: Toolkit already initialized"
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            latch.await();
        } catch (IllegalStateException e) {
            // Toolkit is already running, do nothing
        }
    }
}
