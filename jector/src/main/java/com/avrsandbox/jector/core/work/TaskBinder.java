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
import java.util.HashMap;

/**
 * A bridging API that binds annotated {@link ExecuteOn} methods in a {@link Worker} as 
 * {@link WorkerTask}s to be executed on the registered {@link TaskExecutor}s.
 *
 * <p>
 * 
 * Each TaskBinder instance has its own {@link Worker} implementation, all the {@link ExecuteOn} annotated 
 * methods should be in an object of type {@link Worker} to be executed on the specified {@link TaskExecutor}.
 * 
 * @author pavl_g
 */
public class TaskBinder {

    /**
     * The associated worker implementation.
     */
    protected Worker worker;

    /**
     * A map holding the registered task executors.
     */
    protected HashMap<Class<? extends TaskExecutor>, TaskExecutor> taskExecutors = new HashMap<>();

    /**
     * Instantiates a task binder instance with a worker implementation.
     * 
     * @param worker an instance of the worker interface
     */
    public TaskBinder(Worker worker) {
        this.worker = worker;
    }

    /**
     * Registers a new instance of task executor, replacing the old 
     * executor if called multiple times.
     * 
     * @param taskExecutor an instance of the TaskExecutor to execute annotated worker methods
     */
    public void registerTaskExecutor(TaskExecutor taskExecutor) {
        taskExecutors.put(taskExecutor.getClass(), taskExecutor);
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
        taskExecutors.remove(clazz);
    }

    /**
     * Binds worker methods to their executor instances via WorkerTasks.
     */
    public void bind() {
        bind(null);
    }

    /**
     * Binds worker methods to their executor instances via WorkerTasks.
     * 
     * @param <T> a generic representing the type of the passed arguments, use Object for heterogenous types
     * @param methodArguments a data structure representing a wrapper for methods arguments passed to the 
     *                        methods to be executed
     */
    public <T> void bind(MethodArguments<T> methodArguments) {
        Method[] methods = worker.getClass().getDeclaredMethods();
        for (Method method : methods) {
            /* Retrieves the Annotation of type RunOn */
            ExecuteOn annotation = method.getAnnotation(ExecuteOn.class);
            /* Sanity Check the input */
            if (annotation == null || annotation.executors() == null) {
                continue;
            }
            /* Binds worker methods to the specified task executor  */
            bind(annotation.executors(), method, methodArguments);
        }   
    }

    /**
     * Retrieves the registered task executor instances, use this
     * function to retrieve the {@link WorkerTask} return values when 
     * building a callback API.
     * 
     * @return a map of the registered task executors
     */
    public Map<Class<? extends TaskExecutor>, TaskExecutor> getTaskExecutors() {
        return taskExecutors;
    }

    /**
     * Sets the worker instance associated with this task binder, if this 
     * setter is invoked before calling {@link TaskBinder#bind(MethodArguments)},
     * then dependency injection will be redirected from this new worker implementation.
     * 
     * @param worker a new worker instance
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    /**
     * Retrieves the worker instance associated with this binder.
     * 
     * @return an instance of the worker interface
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Binds a worker method to some task executors via {@link WorkerTask}s.
     * 
     * @param <T> a parameter-generic for the method arguments instance
     * @param <R> a parameter-generic for the method return value
     * @param clazzes task executors key in the map
     * @param method the method to bind
     * @param args the method arguments object
     */
    protected <T, R> void bind(Class<? extends TaskExecutor>[] clazzes, Method method, 
                                               MethodArguments<T> args) {
        for (Class<? extends TaskExecutor> clazz : clazzes) {
            /* Submits a task on the specified executor */
            TaskExecutor taskExecutor = taskExecutors.get(clazz);
            /* Sanity check the executor Object */
            if (taskExecutor == null) {
                continue;
            }
            /* binds the method invokation to the specified executor object */
            taskExecutor.addTask(method, new WorkerTask<R>() {
                @Override
                public R call() {
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

    /**
     * Executes a method passing in its arguments object and the binder object,
     * the signature of the parameterized methods must be [Object method(MethodArguments, TaskBinder)].
     * 
     * @param <T> defines a parameter-generic type for the method arguments
     * @param <R> defines a return-generic type for the method return value
     * @param method the method to execute, should be of the signature 
     *               [Object method(MethodArguments, TaskBinder)]
     * @param args the method arguments data structure
     * @param taskBinder the task binder object
     * @return the return value of the method execution
     */
    @SuppressWarnings("unchecked")
    protected <T, R> R executeMethod(Method method, MethodArguments<T> args, TaskBinder taskBinder) {
        try {
            if (args != null && args.getArgs() != null) {
                Validator.validateParametersLength(method, 2);
                Validator.validateParameterType(method, 0, args.getClass());
                Validator.validateParameterType(method, 1, taskBinder.getClass());
                return (R) method.invoke(worker, args, taskBinder);
            } else {
                return (R) method.invoke(worker, taskBinder);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}