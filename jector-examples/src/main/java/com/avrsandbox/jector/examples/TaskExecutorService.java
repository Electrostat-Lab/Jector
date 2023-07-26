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
package com.avrsandbox.jector.examples;

import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.core.work.TaskExecutor;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.core.work.WorkerTask;
import com.avrsandbox.jector.examples.TestTaskBinder;

/**
 * Provides a worker implementation to inject some callable {@link WorkerTask}s (dependencies)
 * into some registered {@link TaskExecutor}s (dependent objects) using the {@link TaskBinder} api (injector utility).
 * 
 * @author pavl_g
 */
public class TaskExecutorService implements Worker {

    /**
     * Writes a message into the task return.
     */
    @ExecuteOn(executors = {TestTaskBinder.Daemon.class})
    public String writeMessage(MethodArguments<Object> methodArguments, TaskBinder taskBinder) {
        try {
            System.out.println("-----------------------------------------------------");
            System.out.println(Thread.currentThread().getName());
            System.out.println("-----------------------------------------------------");
            return "Hello Jector!";
        } finally {
            /* 6) Activates concurrent tasks describing Jector concurrency model */
            taskBinder.getTaskExecutors()
                      .get(TestTaskBinder.Looper.class)
                      .getTasks()
                      .get("showMessage")
                      .setEnabled(true);
        }
    }

    /**
     * Shows a written message from the task return of another executor.
     */
    @ExecuteOn(executors = {TestTaskBinder.Looper.class})
    public void showMessage(MethodArguments<Object> methodArguments, TaskBinder taskBinder) {
        System.out.println("-----------------------------------------------------");
        System.out.println(Thread.currentThread().getName());
        /* Reterieves the writeMessage return value */
        System.out.println(taskBinder.getTaskExecutors()
                                     .get(TestTaskBinder.Daemon.class)
                                     .getTasks()
                                     .get("writeMessage")
                                     .getResult());
        System.out.println("-----------------------------------------------------");
    }
}
