package com.avrsandbox.jector.examples.monkey;

import com.avrsandbox.jector.core.thread.AppThread;
import com.avrsandbox.jector.core.thread.concurrency.ConcurrentAppThread;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutor;
import com.jme3.app.SimpleApplication;

public final class TestMonkeyTaskBinder extends SimpleApplication {
    
    protected static final AppThread assetLoaderThread = new AssetLoaderThread();
    protected static final TaskExecutorService service = new TaskExecutorService();
    protected static final MonkeyTaskExecutor monkeyTaskExecutor = new MonkeyTaskExecutor("Executor", service);
    protected float timer = 0f;

    public static void main(String[] args) {
        new TestMonkeyTaskBinder().start();
    }

    @Override
    public void simpleInitApp() {
        assetLoaderThread.start();

        monkeyTaskExecutor.registerTaskExecutor(assetLoaderThread);
        monkeyTaskExecutor.registerTaskExecutor(monkeyTaskExecutor);

        assetLoaderThread.setActive(true);
        monkeyTaskExecutor.setActive(true);
        
        stateManager.attach(monkeyTaskExecutor);

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (timer < 0.5f && timer >= 0) {
            timer += tpf;
        } else if (timer != -1){
            monkeyTaskExecutor.getTaskExecutors()
                                .get(AssetLoaderThread.class)
                                .getTasks()
                                .get("cacheAsset")
                                .setActive(true);
            timer = -1f;
        }
    }

    public static class AssetLoaderThread extends ConcurrentAppThread {
        public AssetLoaderThread() {
            super(AssetLoaderThread.class.getName());
            setDaemon(true);
        }
    }
}
