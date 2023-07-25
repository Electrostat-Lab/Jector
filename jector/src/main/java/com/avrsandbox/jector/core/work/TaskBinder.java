package com.avrsandbox.jector.core.work;

import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.work.TaskReceiver;
import com.avrsandbox.jector.util.Validator;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Binds a method task to some threads based on the annotation.
 *
 * @author pavl_g.
 */
public class TaskBinder {

    protected Worker worker;
    protected HashMap<Class<? extends TaskReceiver>, TaskReceiver> taskReceivers = new HashMap<>();

    public TaskBinder(Worker worker) {
        this.worker = worker;
    }

    public void execute() {
        execute(null);
    }

    public <T> void execute(MethodArguments<T> methodArguments) {
        Method[] methods = worker.getClass().getDeclaredMethods();
        for (Method method : methods) {
            /* Retrieves the Annotation of type RunOn */
            ExecuteOn annotation = method.getAnnotation(ExecuteOn.class);
            /* Sanity Check the input */
            if (annotation == null || annotation.receivers() == null) {
                continue;
            }
            /* Binds worker tasks to the specified thread  */
            bind(annotation.receivers(), method, methodArguments, worker);
        }   
    }

    public void registerReceiver(TaskReceiver taskReceiver) {
        taskReceivers.put(taskReceiver.getClass(), taskReceiver);
    }

    public void registerReceiver(Class<? extends TaskReceiver> taskReceiver) 
                throws InstantiationException, IllegalAccessException {
        if (taskReceivers.get(taskReceiver) != null) {
            /* object has been already registered, exiting anyway */
            return;
        }
        registerReceiver(taskReceiver.newInstance());
    }

    public void unregisterReceiver(TaskReceiver taskReceiver) {
        unregisterReceiver(taskReceiver.getClass());
    }

    public HashMap<Class<? extends TaskReceiver>, TaskReceiver> getReceivers() {
        return taskReceivers;
    }

    public void unregisterReceiver(Class<? extends TaskReceiver> taskReceiver) {
        taskReceivers.remove(taskReceiver);
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    protected <A, R> void bind(Class<? extends TaskReceiver>[] taskReceiversClasses, Method method, 
                            MethodArguments<A> args, Worker worker) {
        for (Class<? extends TaskReceiver> taskReceiverClass : taskReceiversClasses) {
            /* Submits a task on the specified thread */
            TaskReceiver taskReceiver = taskReceivers.get(taskReceiverClass);
            /* Sanity check the thread Object */
            if (taskReceiver == null) {
                continue;
            }
            /* binds the method invokation to the specified thread object */
            taskReceiver.addTask(method, new Task() {
                @Override
                public Object call() {
                    /* The Triple Check Pattern (No. of parameters - Input args - Types compatibility) */
                    if (method.getParameters() != null) {
                        return executeMethod(method, args, TaskBinder.this);
                    } else {
                        /* Force null args for non-parameterized methods */
                        return executeMethod(method, null, TaskBinder.this);
                    }
                }
            });
        }
    }

    protected <T> Object executeMethod(Method method, MethodArguments<T> args, TaskBinder taskBinder)
                            throws IllegalStateException, IllegalArgumentException {
        try {
            if (args != null && args.getArgs() != null) {
                Validator.validateParametersLength(method, 2);
                Validator.validateParameterType(method, 0, args.getClass());
                Validator.validateParameterType(method, 1, taskBinder.getClass());
                return method.invoke(worker, args, taskBinder);
            } else {
                return method.invoke(worker, taskBinder);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}