package com.avrsandbox.jector.examples.monkey;

import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.work.TaskBinder;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.monkey.core.command.MonkeyMethodArguments;
import com.avrsandbox.jector.monkey.core.work.*;
import com.jme3.app.SimpleApplication;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public final class TaskExecutorService implements Worker {
    
    @ExecuteOn(executors = {TestMonkeyTaskBinder.AssetLoaderThread.class})
    public Geometry cacheAsset(MethodArguments<Object> args, TaskBinder taskBinder) {
        try {

            Application application = ((MonkeyMethodArguments) args).getTaskExecutor()
                                                                    .getApplication();
            Geometry geom = new Geometry("Box", new Box(1, 1, 1));
            Material mat = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geom.setMaterial(mat);
            System.out.println(Thread.currentThread().getName());
            return geom;
        } finally {
            taskBinder.getTaskExecutors()
                      .get(MonkeyTaskExecutor.class)
                      .getTasks()
                      .get("attachAsset")
                      .setActive(true);
        }
    }

    @ExecuteOn(executors = {MonkeyTaskExecutor.class})
    public void attachAsset(MethodArguments<Object> args, TaskBinder taskBinder) {
        SimpleApplication application = (SimpleApplication) ((MonkeyMethodArguments) args).getTaskExecutor()
                                                                      .getApplication();
        Geometry geom = (Geometry) taskBinder.getTaskExecutors()
                                 .get(TestMonkeyTaskBinder.AssetLoaderThread.class)
                                 .getTasks()
                                 .get("cacheAsset")
                                 .getResult();
        application.getRootNode().attachChild(geom);

        MonkeyWorkerTask task = (MonkeyWorkerTask) taskBinder.getTaskExecutors()
                                                             .get(MonkeyTaskExecutor.class)
                                                             .getTasks().get("attachAsset");    
  

        System.out.println(task.getTimePerFrame());
        taskBinder.getTaskExecutors()
                  .get(MonkeyTaskExecutor.class)
                  .getTasks()
                  .get("attachAsset").setActive(false);
    }
}
