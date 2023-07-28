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

import java.util.concurrent.Callable;

/**
 * Defines a task for a worker object, a task maps a method to be
 * executed in the annotated {@link TaskExecutor}.
 * 
 * @param <T> class-generic type for the return type of this task
 * @author pavl_g
 */
public abstract class WorkerTask<T> implements Callable<T> {

    /**
     * A thread-safe flag to test whether this task has been executed before.
     */
    protected volatile boolean executed;

    /**
     * A thread-safe flag to enable/disable this task.
     */
    protected volatile boolean active = false;

    /**
     * A thread-safe object representing the return value of this task,
     * "null" if a void task is specified.
     */
    protected volatile T result;

    /**
     * Sets the return result of this task, "null" if a task to a void
     * method is specified.
     * 
     * @param result the new result of this task
     */
    public void setResult(T result) {
        this.result = result;
    }

    /**
     * Marks this task as enabled/disabled, default value is "false".
     * 
     * @param enabled true to enable this task, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieves the return result value of this task.
     * 
     * @return the return result of this task, "null" if a void method is 
     *         mapped to this task instance
     */
    public T getResult() {
        return result;
    }

    /**
     * Tests whether this worker task has been executed at least once,
     * default value is "false".
     * 
     * @return true if this worker task should have been executed before
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * Tests whether this task is enabled, default value is "false".
     * 
     * @return true if this task is enabled, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Marks this task as executed before, default value is "false".
     */
    public void setExecuted() {
        this.executed = true;
    }
}
