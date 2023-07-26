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

import java.lang.reflect.Method;
import java.util.Map;
import com.avrsandbox.jector.core.work.WorkerTask;

/**
 * A general-purpose abstraction representing the entity executing the methods (dependencies) in 
 * the form of tasks interface {@link WorkerTask}.
 * 
 * @author pavl_g
 */
public interface TaskExecutor {

    /**
     * Maps a new worker task to its worker method object.
     *  
     * @param <T> a parameter-generic specifying the type of the 
     *            return values of the worker tasks 
     * @param method the method signifying this task
     * @param task a task instance
     */
    <T> void addTask(Method method, WorkerTask<T> task);

    /**
     * Runs the tasks in synchrony.
     */
    void runTasks();

    /**
     * Terminates this task executor. 
     */
    void terminate();

    /**
     * Tests whether this task executor has been terminated.
     * 
     * @return true, if this executor has been terminated, false otherwise
     */
    boolean isTerminated();

    /**
     * Tests whether this executor instance is enabled.
     * 
     * @return true if this executor should be active, false otherwise
     */
    boolean isEnabled();

    /**
     * Sets the activity of this instance.
     * 
     * @param enabled true to be regarded as active, false otherwise
     */
    void setEnabled(boolean enabled);

    /**
     * Retrieves the tasks to be executed by this instance.
     * 
     * @param <T> a parameter-generic specifying the type of the 
     *            return values of the worker tasks
     * @return a map of tasks to be executed
     */
    <T> Map<String, WorkerTask<T>> getTasks();
}
