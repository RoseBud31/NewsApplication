package com.example.newsapplicationversion1.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyManager {
    private static final int THREAD_COUNT = 10;
    private final ExecutorService executorService;

    public ConcurrencyManager() {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    }
    public void submit(Runnable task) {
        executorService.submit(task);
    }
    public void shutdown() {
        executorService.shutdown();
    }
}
