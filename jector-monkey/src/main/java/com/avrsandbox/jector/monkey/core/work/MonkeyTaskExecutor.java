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

import com.avrsandbox.jector.core.work.TaskExecutor;
import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.WorkerTask;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base implementation of the jector {@link TaskExecutor}s to a jMonkeyEngine app state.
 * 
 * @author pavl_g
 */
public class MonkeyTaskExecutor extends BaseAppState implements TaskExecutor {

    /**
     * Tasks wrapping the methods to be bound to their specified annotated methods.
     */
    protected final Map<String, WorkerTask> tasks = new ConcurrentHashMap<>();

    /**
     * A flag to order the executor for termination.
     */
    protected volatile boolean terminate;

    /**
     * A flag to order the executor to start running.
     */
    protected volatile boolean active = false;

    /**
     * Provides a universal tpf attribute for the associated tasks.
     */
    protected float timePerFrame;

    /**
     * Provides an interface to command-state the initialization.
     */
    protected TaskExecutorListeners taskExecutorListeners;

    /**
     * Instantiates a new instance of a task executor specialized to run on the JME thread only.
     *
     * @param id the associated state id
     */
    public MonkeyTaskExecutor(String id) {
        super(id);
    }

    /**
     * Retrieves the application time-per-frame in seconds.
     *
     * @return the JME app tpf in seconds
     */
    public float getTimePerFrame() {
        return timePerFrame;
    }

    /**
     * Adjusts this task executor listeners.
     *
     * @param taskExecutorListeners a new listener instance
     */
    public void setTaskExecutorListeners(TaskExecutorListeners taskExecutorListeners) {
        this.taskExecutorListeners = taskExecutorListeners;
    }

    @Override
    protected void initialize(Application app) {
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onExecutorInitialized(app);
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onExecutorCleanUp(app);
        }
    }

    @Override
    protected void onEnable() {
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onExecutorEnabled(getApplication());
        }
    }

    @Override
    protected void onDisable() {
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onExecutorDisabled(getApplication());
        }
    }

    @Override 
    public void update(float tpf) {
        if (isTerminated()) {
            return;
        }
        this.timePerFrame = tpf;
        /* 2) Run Worker Method tasks */
        executeTasks(tpf);
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
    public void startExecutorService(TaskExecutorsManager taskExecutorsManager) {
        assert taskExecutorsManager instanceof MonkeyTaskExecutorsManager;
        ((MonkeyTaskExecutorsManager) taskExecutorsManager)
                .getApplication().getStateManager().attach(this);
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onStartExecutorService();
        }
    }

    @Override
    public void destructExecutorService(TaskExecutorsManager taskExecutorsManager) {
        this.terminate = true;
        setActive(false);
        getStateManager().detach(this);
        TaskExecutor.super.destructExecutorService(taskExecutorsManager);
        if (taskExecutorListeners != null) {
            taskExecutorListeners.onDestructExecutorService();
        }
    }

    @Override
    public void executeTasks(Object arguments) {
        try {
            for (String task : tasks.keySet()) {
                if (tasks.get(task) == null || !tasks.get(task).isActive()) {
                    continue;
                }
                if (!(tasks.get(task) instanceof MonkeyWorkerTask)) {
                    throw new IllegalArgumentException("WorkerTasks must be of type: " + MonkeyWorkerTask.class.getName());
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
    public Map<String, WorkerTask> getTasks() {
        return tasks;
    }

    @Override
    public boolean isTerminated() {
        return terminate;
    }
}
