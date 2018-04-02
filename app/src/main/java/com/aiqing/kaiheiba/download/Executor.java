package com.aiqing.kaiheiba.download;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
    private static ExecutorService excutor = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        excutor.execute(runnable);
    }
}
