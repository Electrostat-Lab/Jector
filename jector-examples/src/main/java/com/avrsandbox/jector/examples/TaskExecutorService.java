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

import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.TaskExecutor;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.core.work.WorkerTask;

/**
 * Provides a worker implementation to inject some callable {@link WorkerTask}s (dependencies)
 * into some registered {@link TaskExecutor}s (dependent objects) using the {@link TaskExecutorsManager} api (injector utility).
 * 
 * @author pavl_g
 */
public class TaskExecutorService implements Worker {

    /**
     * Writes a message into the task return.
     */
    @ExecuteOn(executors = {TestTaskExecutorManager.DAEMON_THREAD})
    public String writeMessage(MethodArguments methodArguments, TaskExecutorsManager taskExecutorsManager) {
        try {
            System.out.println("-----------------------------------------------------");
            System.out.println(Thread.currentThread().getName());
            System.out.println(methodArguments.getArgs()
                                              .get("message"));
            System.out.println("-----------------------------------------------------");
            return "Hello Jector!";
        } finally {
            /* 6) Activates concurrent tasks describing Jector concurrency model */
            taskExecutorsManager.getTaskExecutors()
                      .get(TestTaskExecutorManager.LOOPER_THREAD)
                      .getTasks()
                      .get("showMessage")
                      .setActive(true);
        }
    }

    /**
     * Shows a written message from the task return of another executor.
     */
    @ExecuteOn(executors = {TestTaskExecutorManager.LOOPER_THREAD})
    public void showMessage(MethodArguments methodArguments, TaskExecutorsManager taskExecutorsManager) {
        System.out.println("-----------------------------------------------------");
        System.out.println(Thread.currentThread().getName());
        /* Retrieves the writeMessage return value */
        System.out.println(taskExecutorsManager.getTaskExecutors()
                                     .get(TestTaskExecutorManager.DAEMON_THREAD)
                                     .getTasks()
                                     .get("writeMessage")
                                     .getResult());
        System.out.println("-----------------------------------------------------");

        System.exit(0);
    }
}
