/* 
* BSD 3-Clause License
*
* Copyright (c) 2023, The AvrSandbox Project, Jector Framework
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
*    and/or other materials provided with the distribution.
*
* 3. Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.avrsandbox.jector.core.work;

import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.util.Validator;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.IllegalAccessException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A bridging API that binds annotated {@link ExecuteOn} methods in a {@link Worker} as 
 * {@link WorkerTask}s to be executed on the registered {@link TaskExecutor}s.
 *
 * <p> Each TaskExecutorManager instance has its own {@link Worker}s implementation, all the {@link ExecuteOn} annotated
 * methods should be in an object of type {@link Worker} to be executed on the declared {@link TaskExecutor}.
 * 
 * @author pavl_g
 */
public class TaskExecutorsManager {

    /**
     * The associated workers implementation.
     */
    protected Worker[] workers;

    /**
     * A thread-safe modifiable map holding the registered task executors.
     */
    protected Map<String, TaskExecutor> taskExecutors = new ConcurrentHashMap<>();

    /**
     * Instantiates an executors manager with a single worker implementation.
     *
     * @param worker an instance of worker
     */
    public TaskExecutorsManager(Worker worker) {
        this(new Worker[] {worker});
    }

    /**
     * Instantiates an executors manager with workers implementations.
     * 
     * @param workers an array of the worker instances
     */
    public TaskExecutorsManager(Worker[] workers) {
        this.workers = workers;
    }

    /**
     * Registers an instance of task executor invoking the {@link TaskExecutor#startExecutorService(TaskExecutorsManager)}.
     *
     * @param name the name of the task executor to register in string format
     * @param taskExecutor an instance of the TaskExecutor to execute annotated worker methods
     * @throws IllegalArgumentException if invoked more than once on the same task executor object
     */
    public void registerTaskExecutor(String name, TaskExecutor taskExecutor) {
        if (taskExecutors.get(name) == taskExecutor) {
            throw new IllegalArgumentException("TaskExecutor is already registered!");
        }
        taskExecutors.put(name, taskExecutor);
        taskExecutor.startExecutorService(this);
    }

    /**
     * Creates and registers a new instance of the {@link TaskExecutor} invoking
     * {@link TaskExecutor#startExecutorService(TaskExecutorsManager)}.
     *
     * @param name the name of the task executor to register in string format
     * @param clazz a class of type TaskExecutor
     * @throws InstantiationException if the class that declares the underlying constructor represents an abstract class
     * @throws IllegalAccessException if this Constructor object is enforcing Java language access control and the underlying constructor is inaccessible
     * @throws NoSuchMethodException thrown when a particular constructor cannot be found
     * @throws InvocationTargetException if the underlying constructor throws an exception (checked)
     */
    public void registerTaskExecutor(String name, Class<? extends TaskExecutor> clazz)
                throws InstantiationException, IllegalAccessException, 
                       NoSuchMethodException, InvocationTargetException {
        registerTaskExecutor(name, clazz.getDeclaredConstructor().newInstance());
    }

    /**
     * Unregisters a task executor invoking the {@link TaskExecutor#destructExecutorService(TaskExecutorsManager)}.
     *
     * @param name the name of the task executor to unregister in string format
     * @throws IllegalArgumentException if the task executor is not found
     */
    public void unregisterTaskExecutor(String name) {
        if (taskExecutors.get(name) == null) {
            throw new IllegalArgumentException(name + " TaskExecutor is not found!");
        }
        taskExecutors.get(name).destructExecutorService(this);
        taskExecutors.remove(name);
    }

    /**
     * Binds worker methods to their executor instances via {@link WorkerTask}s.
     */
    public void bind() {
        bind(null);
    }

    /**
     * Binds worker methods to their executor instances via {@link WorkerTask}s.
     * 
     * @param methodArguments a data structure representing a wrapper for methods arguments passed to the 
     *                        methods to be executed
     */
    public void bind(MethodArguments methodArguments) {
        for (Worker worker : workers) {
            Method[] methods = worker.getClass().getDeclaredMethods();
            for (Method method : methods) {
                /* Retrieves the Annotation of type RunOn */
                ExecuteOn annotation = method.getAnnotation(ExecuteOn.class);
                /* Sanity Check the input */
                if (annotation == null || annotation.executors().length < 1) {
                    continue;
                }
                /* Binds worker methods to the specified task executor  */
                bind(annotation.executors(), worker, method, methodArguments);
            }
        }
    }

    /**
     * Retrieves the registered task executor instances, use this
     * function to retrieve the {@link WorkerTask} return values when 
     * building a callback API.
     * 
     * @return a map of the registered task executors
     */
    public Map<String, TaskExecutor> getTaskExecutors() {
        return taskExecutors;
    }

    /**
     * Sets the worker instance associated with this instance, if this
     * setter is invoked before calling {@link TaskExecutorsManager#bind(MethodArguments)},
     * then dependency injection will be redirected from those new workers implementation.
     * 
     * @param workers a new workers array
     */
    public void setWorkers(Worker[] workers) {
        this.workers = workers;
    }

    /**
     * Retrieves the worker instance associated with this instance.
     * 
     * @return an instance of the worker interface
     */
    public Worker[] getWorkers() {
        return workers;
    }

    /**
     * Binds a worker method to some task executors via {@link WorkerTask}s.
     * 
     * @param executors task executors key in the map
     * @param worker the worker class containing the runnable annotated methods
     * @param method the method to bind
     * @param args the method arguments object
     */
    protected void bind(String[] executors, Worker worker, Method method,
                                               MethodArguments args) {
        for (String executorName : executors) {
            /* Submits a task on the specified executor */
            TaskExecutor taskExecutor = taskExecutors.get(executorName);
            bind(taskExecutor, worker, method, args);
        }
    }

    /**
     * Binds a method to a task executor instance by wrapping it into a {@link WorkerTask}.
     *
     * @param taskExecutor the executor instance
     * @param worker the worker class containing the runnable annotated methods
     * @param method the method to wrap as a task
     * @param args the method arguments, or null for nullary methods
     */
    protected void bind(TaskExecutor taskExecutor, Worker worker, Method method, MethodArguments args) {
        /* binds the method invocation to the specified executor object */
        taskExecutor.addTask(method, new WorkerTask() {
            @Override
            public Object call() {
                /* The Triple Check Pattern (No. of parameters - Input args - Types compatibility) */
                if (method.getParameters() != null) {
                    return executeMethod(worker, method, args, TaskExecutorsManager.this);
                } else {
                    /* Force null args for non-parameterized methods */
                    return executeMethod(worker, method, null, TaskExecutorsManager.this);
                }
            }
        });
    }

    /**
     * Executes a method passing in its arguments object and the binder object,
     * the signature of the parameterized methods must be [Object method(MethodArguments, TaskBinder)].
     *
     * @param worker the worker class containing the runnable annotated methods
     * @param method the method to execute, should be of the signature 
     *               [Object method(MethodArguments, TaskBinder)]
     * @param args the method arguments data structure
     * @param taskExecutorsManager the task binder object
     * @return the return value of the method execution
     */
    protected Object executeMethod(Worker worker, Method method, MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        try {
            if (args != null && args.getArgs() != null) {
                Validator.validateParametersLength(method, 2);
                Validator.validateParameterType(method, 0, args.getClass());
                Validator.validateParameterType(method, 1, taskExecutorsManager.getClass());
                return method.invoke(worker, args, taskExecutorsManager);
            } else {
                return method.invoke(worker, taskExecutorsManager);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}