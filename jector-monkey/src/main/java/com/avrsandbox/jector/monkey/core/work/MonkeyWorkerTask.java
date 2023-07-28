
package com.avrsandbox.jector.monkey.core.work;

import com.avrsandbox.jector.core.work.WorkerTask;

public abstract class MonkeyWorkerTask extends WorkerTask<Object> {
    protected float timePerFrame;

    public void setTimePerFrame(float timePerFrame) {
        this.timePerFrame = timePerFrame;
    }

    public float getTimePerFrame() {
        return timePerFrame;
    }
}
