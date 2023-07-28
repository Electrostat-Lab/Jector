
package com.avrsandbox.jector.monkey.core.work;

import com.avrsandbox.jector.monkey.core.command.MonkeyMethodArguments;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.core.work.*;
import java.lang.reflect.Method;

public class MonkeyTaskBinder extends TaskBinder {
    
    public MonkeyTaskBinder(Worker worker) {
        super(worker);
    }

    @Override
    public <T> void bind(MethodArguments<T> methodArguments) {
        if (!Thread.currentThread().getName().equals("jME3 Main")) {
            throw new IllegalStateException("Cannot bind on non-application thread!");
        }
        super.bind(methodArguments);   
    }

    @Override
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
            taskExecutor.addTask(method, new MonkeyWorkerTask() {
                @Override
                public R call() {
                    /* The Triple Check Pattern (No. of parameters - Input args - Types compatibility) */
                    if (method.getParameters() != null) {
                        return executeMethod(method, args, MonkeyTaskBinder.this);
                    } else {
                        /* Force null args for non-parameterized methods */
                        return executeMethod(method, null, MonkeyTaskBinder.this);
                    }
                }
            });
        }
    }
}
