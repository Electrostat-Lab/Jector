package com.avrsandbox.jector.examples;

import com.avrsandbox.jector.core.thread.AppThread;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.concurrency.ConcurrentAppThread;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.core.work.Worker;

public final class TestTaskBinder {

    private static final AppThread looperThread = new Looper();
    private static final AppThread daemonThread = new Daemon();
    private static final AppThread foregroundThread = new Foreground();
    private static final TaskBinder taskBinder = new TaskBinder(new TaskExecutorService());

    public static void main(String[] args) throws InterruptedException {
        /* Start threads */
        daemonThread.start();
        looperThread.start();
        foregroundThread.start();

        taskBinder.registerReceiver(daemonThread);
        taskBinder.registerReceiver(looperThread);
        taskBinder.registerReceiver(foregroundThread);
        taskBinder.execute(new MethodArguments<String>(new String[]{"Hello", "World"}));
    }

    public static class Looper extends ConcurrentAppThread {
        public Looper() {
            super(Looper.class.getName());
            setDaemon(false);
        }
    }
    
    public static class Daemon extends ConcurrentAppThread {
        public Daemon() {
            super(Daemon.class.getName());
            setDaemon(true);
        }
    }

    public static class Foreground extends ConcurrentAppThread {
        public Foreground() {
            super(Foreground.class.getName());
            setDaemon(true);
        }
    }
}
