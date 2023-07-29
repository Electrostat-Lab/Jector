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

package com.avrsandbox.jector.monkey.core.work;

import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.core.work.*;
import java.lang.reflect.Method;

/**
 * A JME-thread-safe specialized implementation of the {@link TaskExecutorsManager} for jMonkeyEngine.
 *
 * @author pavl_g
 */
public class MonkeyTaskExecutorsManager extends TaskExecutorsManager {

    /**
     * Instantiates a JME task binder with a single worker instance.
     *
     * @param worker the worker instance holding the worker methods to be executed
     */
    public MonkeyTaskExecutorsManager(Worker worker) {
        super(worker);
    }

    /**
     * Instantiates a JME task binder with multiple worker instances.
     *
     * @param workers an array of worker instances
     */
    public MonkeyTaskExecutorsManager(Worker[] workers) {
        super(workers);
    }

    @Override
    public void bind(MethodArguments methodArguments) {
        if (!Thread.currentThread().getName().equals("jME3 Main")) {
            throw new IllegalStateException("Cannot bind on a non-application thread!");
        }
        super.bind(methodArguments);
    }

    @Override
    protected void bind(TaskExecutor taskExecutor, Worker worker, Method method,
                                            MethodArguments args) {
        /* binds the method invocation to the specified executor object */
        taskExecutor.addTask(method, new MonkeyWorkerTask() {
            @Override
            public Object call() {
                /* The Triple Check Pattern (No. of parameters - Input args - Types compatibility) */
                if (method.getParameters() != null) {
                    return executeMethod(worker, method, args, MonkeyTaskExecutorsManager.this);
                } else {
                    /* Force null args for non-parameterized methods */
                    return executeMethod(worker, method, null, MonkeyTaskExecutorsManager.this);
                }
            }
        });
    }
}
