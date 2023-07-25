package com.avrsandbox.jector.core.work;

import java.util.concurrent.Callable;

public abstract class Task implements Callable<Object> {
    protected volatile boolean executed;
    protected volatile Object result;

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted() {
        this.executed = true;
    }
}
