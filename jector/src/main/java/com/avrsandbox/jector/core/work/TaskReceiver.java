package com.avrsandbox.jector.core.work;

import java.lang.reflect.Method;
import java.util.Map;
import com.avrsandbox.jector.core.work.Task;

public interface TaskReceiver {
    void addTask(Method method, Task task);
    void runTasks() throws Exception;
    void terminate();
    boolean isTerminated();
    Map<String, Task> getTasks();
}
