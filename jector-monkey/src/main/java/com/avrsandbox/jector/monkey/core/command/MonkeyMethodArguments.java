
package com.avrsandbox.jector.monkey.core.command;

import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutor;

public class MonkeyMethodArguments extends MethodArguments<Object> {
    protected final String TASK_EXECUTOR = "MONKEY_EXECUTOR";

    public MonkeyMethodArguments() {
        super();
    }

    public void setTaskExecutor(MonkeyTaskExecutor executor) {
        args.put(TASK_EXECUTOR, executor);
    }

    public MonkeyTaskExecutor getTaskExecutor() {
        return (MonkeyTaskExecutor) args.get(TASK_EXECUTOR);
    }
}
