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

package com.avrsandbox.jector.core.thread.concurrency;

import com.avrsandbox.jector.core.thread.AppThread;
import com.avrsandbox.jector.core.work.WorkerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.Method;

/**
 * A thread-safe implementation of the {@link AppThread}.
 * 
 * @author pavl_g 
 */
public class ConcurrentAppThread extends AppThread {

    /**
     * Provides a monitor object for thread-safety.
     */
    protected final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * Instantiates a thread-safe instance of the AppThread.
     * 
     * @param name the name of the thread
     */
    public ConcurrentAppThread(String name) {
        super(name);
    }

    @Override
    public void addTask(Method method, WorkerTask task) {
        try {
            reentrantLock.lock();
            super.addTask(method, task);
        } finally {
            reentrantLock.unlock();
        }    
    }

    @Override
    public void executeTasks(Object arguments) {
        try {
            reentrantLock.lock();
            super.executeTasks(arguments);
        } finally {
            reentrantLock.unlock();
        }
    }
}