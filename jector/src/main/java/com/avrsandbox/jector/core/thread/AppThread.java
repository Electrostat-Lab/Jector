package com.avrsandbox.jector.core.thread;

import com.avrsandbox.jector.core.work.Task;
import com.avrsandbox.jector.core.work.TaskReceiver;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the base class of an app thread, the dependent (receiver)
 * object in this DI framework.
 * 
 * <p>
 * 
 * Annotated methods with {@link com.avrsandbox.jector.core.command.ExecuteOn} inside a {@link com.avrsandbox.jector.core.work.Worker}
 * are submitted as tasks to be executed on the specified implementations of the AppThread, the implementations' classes are 
 * specified by annotating them in the array {@link com.avrsandbox.jector.core.command.ExecuteOn#threads()}.
 *
 * @author pavl_g
 */
public abstract class AppThread extends Thread implements TaskReceiver {

    /**
     * Tasks wrapping the methods to be bound to their specified annotated methods.
     */
    protected final Map<String, Task> tasks = new HashMap<>();
    protected volatile boolean terminate;
    protected final String name;

    public AppThread(String name) {
        super(name);
        this.name = name;
    }

    @Override
    public void run() {
        while (!isTerminated()){
            try {
                runTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a new task to the tasks stack.
     *
     * @param task a task instance
     */
    @Override
    public void addTask(Method method, Task task) {
        tasks.putIfAbsent(method.getName(), task);
    }

    @Override
    public void runTasks() throws Exception {
        for (String task : tasks.keySet()) {
            if (tasks.get(task).isExecuted()) {
                continue;
            }
            /* Saves the result of the execution order! */
            tasks.get(task).setResult(tasks.get(task).call());
            tasks.get(task).setExecuted();
        }
    }

    @Override
    public Map<String, Task> getTasks() {
        return tasks;
    }

    @Override
    public void terminate() {
        this.terminate = true;
        System.out.println("IPBinder: AppThread " + name + " is terminated.");
    }

    @Override
    public boolean isTerminated() {
        return terminate;
    }
}
