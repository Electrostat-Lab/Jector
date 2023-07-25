package com.avrsandbox.jector.core.concurrency;

import java.util.concurrent.locks.ReentrantLock;

import com.avrsandbox.jector.core.thread.AppThread;
import com.avrsandbox.jector.core.work.Task;
import com.avrsandbox.jector.core.work.TaskReceiver;
import java.lang.reflect.Method;

/**
 * A thread-safe implementation of the {@link AppThread}.
 * 
 * @author pavl_g 
 */
public class ConcurrentAppThread extends AppThread {

    protected final ReentrantLock reentrantLock = new ReentrantLock();

    public ConcurrentAppThread(String name) {
        super(name);
    }

    @Override
    public void addTask(Method method, Task task) {
        try {
            reentrantLock.lock();
            super.addTask(method, task);
        } finally {
            reentrantLock.unlock();
        }    }

    @Override
    public void runTasks() throws Exception {
        try {
            reentrantLock.lock();
            super.runTasks();
        } finally {
            reentrantLock.unlock();
        }
    }
}
