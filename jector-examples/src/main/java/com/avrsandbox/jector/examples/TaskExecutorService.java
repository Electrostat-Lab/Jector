package com.avrsandbox.jector.examples;

import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import com.avrsandbox.jector.core.work.Task;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.examples.TestTaskBinder;

public class TaskExecutorService implements Worker {

    protected static final ReentrantLock reentrantLock = new ReentrantLock();

    @ExecuteOn(receivers = {TestTaskBinder.Daemon.class})
    public String writeMessage(MethodArguments<Object> methodArguments, TaskBinder taskBinder) {
        try {
            reentrantLock.lock();
            System.out.println("-----------------------------------------------------");
            System.out.println(Thread.currentThread().getName());
            System.out.println("-----------------------------------------------------");
        } finally {
            reentrantLock.unlock();
            return "Hello World";
        }
    }

    @ExecuteOn(receivers = {TestTaskBinder.Looper.class})
    public void showMessage(MethodArguments<Object> methodArguments, TaskBinder taskBinder) {
        try {
            Thread.sleep(500);
            reentrantLock.lock();
            System.out.println("-----------------------------------------------------");
            System.out.println(Thread.currentThread().getName());
            /* Reterieves the writeMessage return value */
            System.out.println(taskBinder.getReceivers()
                                         .get(TestTaskBinder.Daemon.class)
                                         .getTasks()
                                         .get("writeMessage")
                                         .getResult());
            System.out.println("-----------------------------------------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }
}
