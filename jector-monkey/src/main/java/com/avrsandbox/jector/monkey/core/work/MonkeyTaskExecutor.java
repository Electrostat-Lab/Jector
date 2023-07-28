
package com.avrsandbox.jector.monkey.core.work;

import com.avrsandbox.jector.monkey.core.command.MonkeyMethodArguments;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskBinder;
import com.avrsandbox.jector.core.work.WorkerTask;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.core.work.TaskExecutor;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.util.SafeArrayList;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;

/**
 * A base implementation of the jector {@link TaskExecutor}s to a jMonkeyEngine app state.
 * 
 * @author pavl_g
 */
public class MonkeyTaskExecutor extends BaseAppState implements TaskExecutor {
    
    /**
     * Tasks wrapping the methods to be bound to their specified annotated methods.
     */
    protected final Map<String, WorkerTask<Object>> tasks = new HashMap<>();


    protected MonkeyTaskBinder taskBinder;

    /**
     * A flag to order the executor for termination.
     */
    protected volatile boolean terminate;

    /**
     * A flag to order the executor to start running.
     */
    protected volatile boolean active = false;


    protected final MonkeyMethodArguments methodArguments = new MonkeyMethodArguments();

    
    public MonkeyTaskExecutor(String id, Worker worker) {
        super(id);
        taskBinder = new MonkeyTaskBinder(worker);
        methodArguments.setTaskExecutor(this);
    }

    /**
     * Registers a new instance of task executor, replacing the old 
     * executor if called multiple times.
     * 
     * @param taskExecutor an instance of the TaskExecutor to execute annotated worker methods
     */
    public void registerTaskExecutor(TaskExecutor taskExecutor) {
        taskBinder.getTaskExecutors().put(taskExecutor.getClass(), taskExecutor);
    }

    /**
     * Creates a new instance of the {@link TaskExecutor}
     * and adds it to the map of the registered executors replacing 
     * the old instance if called multiple times.
     * 
     * @param clazz a class of type TaskExecutor
     * @throws InstantiationException if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException if this Constructor object is enforcing Java language access control and the underlying constructor is inaccessible
     * @throws NoSuchMethodException thrown when a particular constructor cannot be found
     * @throws InvocationTargetException if the underlying constructor throws an exception (checked)
     */
    public void registerTaskExecutor(Class<? extends TaskExecutor> clazz) 
                throws InstantiationException, IllegalAccessException, 
                       NoSuchMethodException, InvocationTargetException {
        registerTaskExecutor(clazz.getDeclaredConstructor().newInstance());
    }

    /**
     * Unregisters a task executor.
     * 
     * @param taskExecutor a task executor instance to un-register
     */
    public void unregisterTaskExecutor(TaskExecutor taskExecutor) {
        unregisterTaskExecutor(taskExecutor.getClass());
    }

    /**
     * Unregisters a task executor directly using its class object.
     * 
     * @param clazz the class object of the task executor to be unregistered
     */
    public void unregisterTaskExecutor(Class<? extends TaskExecutor> clazz) {
        taskBinder.getTaskExecutors().remove(clazz);
    }

    /**
     * Retrieves the registered task executor instances, use this
     * function to retrieve the {@link WorkerTask} return values when 
     * building a callback API.
     * 
     * @return a map of the registered task executors
     */
    public Map<Class<? extends TaskExecutor>, TaskExecutor> getTaskExecutors() {
        return taskBinder.getTaskExecutors();
    }

    MonkeyMethodArguments getMethodArguments() {
        return methodArguments;
    }

    @Override
    protected void initialize(Application app) {
        taskBinder.bind(methodArguments);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override 
    public void update(float tpf) {
        /* 2) Run Worker Method tasks */
        executeTasks((float) tpf);
    }

    @Override
    public boolean isActive() {
        return isEnabled();
    }

    @Override
    public void setActive(boolean active) {
        setEnabled(active);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void addTask(Method method, WorkerTask<T> task) {
        tasks.put(method.getName(), (WorkerTask<Object>) task);
    }

    @Override
    public void executeTasks(Object arguments) {
        try {
            for (String task : tasks.keySet()) {
                if (!tasks.get(task).isActive()) {
                    continue;
                }
                if (!(tasks.get(task) instanceof MonkeyWorkerTask)) {
                    continue;
                }
                MonkeyWorkerTask monkeyTask = (MonkeyWorkerTask) tasks.get(task);
                /* Invokes and Saves the result of the execution order! */
                monkeyTask.setTimePerFrame((float) arguments);
                monkeyTask.setResult(monkeyTask.call());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, WorkerTask<Object>> getTasks() {
        return tasks;
    }

    @Override
    public void terminate() {
        getStateManager().detach(this);
        this.terminate = true;
    }

    @Override
    public boolean isTerminated() {
        return terminate;
    }
}
