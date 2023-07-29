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

import com.avrsandbox.jector.core.work.WorkerTask;

/**
 * A specialized implementation of the jector WorkerTask for jMonkeyEngine
 * supporting time-per-frame monitoring.
 *
 * @author pavl_g
 */
public abstract class MonkeyWorkerTask extends WorkerTask {

    /**
     * Value for time per frame in seconds, this value
     * is internally synchronized with the JME update thread.
     */
    protected float timePerFrame;

    /**
     * Instantiates a JME worker task mapping a method to be executed
     * on the JME-3 Main thread.
     */
    public MonkeyWorkerTask() {
        super();
    }

    /**
     * Updates the time-per-frame value (in seconds).
     *
     * @param timePerFrame the new value (in seconds)
     */
    public void setTimePerFrame(float timePerFrame) {
        this.timePerFrame = timePerFrame;
    }

    /**
     * Retrieves the time-per-frame value (in seconds).
     *
     * @return the time-per-frame value (in seconds)
     */
    public float getTimePerFrame() {
        return timePerFrame;
    }
}
