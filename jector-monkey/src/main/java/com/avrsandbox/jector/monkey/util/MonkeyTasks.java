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

package com.avrsandbox.jector.monkey.util;

import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutorsManager;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutor;
import com.avrsandbox.jector.monkey.core.work.MonkeyWorkerTask;
import com.avrsandbox.jector.util.Tasks;
import com.jme3.app.SimpleApplication;

/**
 * An extension utility providing a specialization for the {@link Tasks} utility to be
 * utilized within a jMonkeyEngine application.
 *
 * @author pavl_g
 */
public final class MonkeyTasks {

    private MonkeyTasks() {
    }

    /**
     * Retrieves a JME task executor by its name.
     *
     * @param taskExecutorsManager the manager holding the registered executors (non-nullable)
     * @param executor the name of the task-executor (as registered) to retrieve (non-nullable)
     * @return the matching JME task executor (non-nullable)
     */
    public static MonkeyTaskExecutor getTaskExecutor(TaskExecutorsManager taskExecutorsManager,
                                                     String executor) {
        return ((MonkeyTaskExecutor) Tasks
                .getTaskExecutorFromTaskBinder(taskExecutorsManager, executor));
    }

    /**
     * Retrieves a JME worker task from its task executor by its name, the name of the
     * worker task is the same as its corresponding annotated worker method.
     *
     * @param taskExecutorsManager the manager holding the registered executors
     * @param executor the name of the task-executor (as registered) (non-nullable)
     * @param task the name of the task to retrieve (usually the name of the worker method) (non-nullable)
     * @return a reference to the JME worker task (non-nullable)
     */
    public static MonkeyWorkerTask getWorkerTask(TaskExecutorsManager taskExecutorsManager,
                                                 String executor,
                                                 String task) {
        return ((MonkeyWorkerTask) Tasks.getWorkerTask(taskExecutorsManager, executor, task));
    }

    /**
     * Retrieves the JME application instance, the app instance could by fetched from any JME
     * task executor.
     *
     * @param taskExecutorsManager the manager holding the registered executors (of {@link MonkeyTaskExecutorsManager} type)
     * @param executor the name of the task-executor (as registered) (non-nullable)
     * @return a reference to the application instance (non-nullable)
     * @throws IllegalArgumentException if the taskExecutorsManager is not of type MonkeyTaskExecutorsManager
     * @throws IllegalStateException if the JME-3 application instance is null
     */
    public static SimpleApplication getApplication(TaskExecutorsManager taskExecutorsManager,
                                                   String executor) {
        if (!(taskExecutorsManager instanceof MonkeyTaskExecutorsManager)) {
            throw new IllegalArgumentException("Cannot retrieve a JME App Instance from non-application executors!");
        }
        SimpleApplication application = (SimpleApplication) MonkeyTasks.getTaskExecutor(taskExecutorsManager, executor).getApplication();
        if (application == null) {
            throw new IllegalStateException("JME-3 App instance cannot be null!");
        }
        return application;
    }

    /**
     * Retrieves the application time-per-frame as monitored by a registered
     * JME task executor.
     *
     * @param taskExecutorsManager the manager holding the registered executors (non-nullable)
     * @param executor the name of the task-executor (as registered) (non-nullable)
     * @return a reference to the application tpf value
     */
    public static float getApplicationTimePerFrame(TaskExecutorsManager taskExecutorsManager,
                                                   String executor) {
        return MonkeyTasks.getTaskExecutor(taskExecutorsManager, executor).getTimePerFrame();
    }

    /**
     * Retrieves the application time-per-frame as monitored by a JME
     * worker task.
     *
     * @param taskExecutorsManager the manager holding the registered executors (non-nullable)
     * @param executor the name of the task-executor (as registered) (non-nullable)
     * @param task the name of the JME worker task to retrieve its tpf value (non-nullable)
     * @return the value of the tpf as monitored by the specified JME worker task
     */
    public static float getWorkerTaskTimePerFrame(TaskExecutorsManager taskExecutorsManager,
                                                  String executor,
                                                  String task) {
        return MonkeyTasks.getWorkerTask(taskExecutorsManager, executor, task).getTimePerFrame();
    }
}