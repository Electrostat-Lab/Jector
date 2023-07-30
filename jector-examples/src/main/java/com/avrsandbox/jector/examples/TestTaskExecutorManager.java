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

import com.avrsandbox.jector.core.thread.AppThread;
import java.util.HashMap;
import java.util.Map;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.work.TaskExecutorsManager;

/**
 * A live example for the Jector Framework concurrency model.
 * 
 * @author pavl_g
 */
public final class TestTaskBinder {

    private static final AppThread looperThread = new Looper();
    private static final AppThread daemonThread = new Daemon();
    private static final AppThread foregroundThread = new Foreground();
    private static final TaskExecutorsManager taskExecutorsManager = new TaskExecutorsManager(new TaskExecutorService());
    protected static final String DAEMON_THREAD = "DAEMON_THREAD";
    protected static final String LOOPER_THREAD = "LOOPER_THREAD";
    protected static final String FOREGROUND_THREAD = "FOREGROUND_THREAD";

    public static void main(String[] args) throws InterruptedException {
        /* 1) Start threads */
        daemonThread.start();
        looperThread.start();
        foregroundThread.start();

        /* 2) Register executors */
        taskExecutorsManager.registerTaskExecutor(DAEMON_THREAD, daemonThread);
        taskExecutorsManager.registerTaskExecutor(LOOPER_THREAD, looperThread);
        taskExecutorsManager.registerTaskExecutor(FOREGROUND_THREAD, foregroundThread);

        /* 3) Binds worker methods to their executors via worker tasks */
        Map<String, Object> methodArgs = new HashMap<>();
        methodArgs.put("message", "Hello World!");
        taskExecutorsManager.bind(new MethodArguments(methodArgs));
        
        /* 4) Enables executors */
        looperThread.setActive(true);
        daemonThread.setActive(true);

        /* 5) Triggers tasks to run */
        taskExecutorsManager.getTaskExecutors()
                  .get(DAEMON_THREAD)
                  .getTasks()
                  .get("writeMessage")
                  .setActive(true);
    }

    public static class Looper extends AppThread {
        public Looper() {
            super(Looper.class.getName());
            setDaemon(false);
        }
    }
    
    public static class Daemon extends AppThread {
        public Daemon() {
            super(Daemon.class.getName());
            setDaemon(true);
        }
    }

    public static class Foreground extends AppThread {
        public Foreground() {
            super(Foreground.class.getName());
            setDaemon(true);
        }
    }
}
