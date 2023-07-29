# <img src="https://github.com/Software-Hardware-Codesign/Jector/assets/60224159/dcaa688b-ddc1-4534-84e7-79f7da91f1a6" width=60 length=60/> Jector [![Codacy Badge](https://app.codacy.com/project/badge/Grade/3fe7dc8b13ec42a7a15e4c851d90f47e)](https://app.codacy.com/gh/Software-Hardware-Codesign/Jector/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-test.yml/badge.svg)]() [![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-deploy.yml/badge.svg)]()

An advanced DI framework for JVM and Android applications based on the Java Reflection API with a specialized implementation for jMonkeyEngine Applications.

## Building:
```bash
┌─[pavl-machine@pavl-machine]─[/home/twisted/GradleProjects/Jector]
└──╼ $./gradlew :jector:build && \
      ./gradlew :jector:generateSourcesJar && \
      ./gradlew :jector:generateJavadocJar
```

## Quick Testing:
```bash
┌─[pavl-machine@pavl-machine]─[/home/twisted/GradleProjects/Jector]
└──╼ $./gradlew :jector-examples:run
```

## Utilizing in jMonkeyEngine Applications
- build.gradle:
```bash
repositories {
    mavenCentral()
}
dependencies {
    implementation "io.github.software-hardware-codesign:jector:incubator-2"
    implementation "io.github.software-hardware-codesign:jector-monkey:incubator-2"
    implementation "org.jmonkeyengine:jme3-core:3.6.1-stable"
    implementation "org.jmonkeyengine:jme3-desktop:3.6.1-stable"
    implementation "org.jmonkeyengine:jme3-lwjgl3:3.6.1-stable"
}
```
- jMonkeyEngine Application: 
```java
public final class TestMonkeyTaskManager extends SimpleApplication implements OnExecutorInitialized {

    protected static final AppThread assetLoaderThread = new AssetLoaderThread();
    protected static final MonkeyTaskExecutorsManager monkeyTaskBinder =
                                                        new MonkeyTaskExecutorsManager(new TestJectorInheritance());
    protected static final MonkeyTaskExecutor monkeyTaskExecutor =
                                                new MonkeyTaskExecutor("MonkeyExecutor");

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);

        TestMonkeyTaskManager app = new TestMonkeyTaskManager();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetLoaderThread.start();

        monkeyTaskBinder.registerTaskExecutor(assetLoaderThread);
        monkeyTaskBinder.registerTaskExecutor(monkeyTaskExecutor);

        assetLoaderThread.setActive(true);
        monkeyTaskExecutor.setActive(true);
        monkeyTaskExecutor.setOnInitialized(this);
        stateManager.attach(monkeyTaskExecutor);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void onInitialized(Application application) {
        monkeyTaskBinder.bind(new MethodArguments());
        monkeyTaskBinder.getTaskExecutors()
                .get(AssetLoaderThread.class)
                .getTasks()
                .get("setupSky")
                .setActive(true);
    }

    /**
     * An internal daemon thread for async heavy duty asset loading.
     */
    public static class AssetLoaderThread extends ConcurrentAppThread {
        public AssetLoaderThread() {
            super(AssetLoaderThread.class.getName());
            setDaemon(true);
        }
    }
}
```

- Jector Worker Class: 
```java
public class TaskExecutorService implements Worker {

    @ExecuteOn(executors = {TestMonkeyTaskBinder.AssetLoaderThread.class})
    public Geometry setupSky(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        try {
            SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, MonkeyTaskExecutor.class);

            Geometry sky = (Geometry) SkyFactory.createSky(app.getAssetManager(),
                    app.getAssetManager().loadTexture("assets/Textures/sky.jpg"), Vector3f.UNIT_XYZ,
                                                                  SkyFactory.EnvMapType.EquirectMap);
            sky.setLocalScale(0.5f);
            sky.getMaterial().getAdditionalRenderState().setDepthFunc(RenderState.TestFunction.LessOrEqual);

            return sky;
        } finally {
            ApplicationTasks.setSetupSceneTaskActive(taskExecutorsManager, true);
        }
    }

    @ExecuteOn(executors = {MonkeyTaskExecutor.class})
    public void setupScene(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, MonkeyTaskExecutor.class);
        Geometry sky = Tasks.getWorkerTaskResult(taskExecutorsManager,
                                                 TestMonkeyTaskBinder.AssetLoaderThread.class, "setupSky");
        app.getRootNode().attachChild(sky);

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        app.getRootNode().addLight(ambientLight);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, 0.5f, 0.5f));
        dl.setColor(ColorRGBA.White);
        app.getRootNode().addLight(dl);

        /* Disables this task */
        ApplicationTasks.setSetupSceneTaskActive(taskExecutorsManager, false);

        /* Activates the concurrent task */
        ApplicationTasks.setCacheAssetTaskActive(taskExecutorsManager, true);
    }

    @ExecuteOn(executors = {TestMonkeyTaskBinder.AssetLoaderThread.class})
    public Spatial cacheAsset(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        try {
            /* Retrieves cached JME app */
            SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, MonkeyTaskExecutor.class);
            Spatial dataBaseStack = app.getAssetManager().loadModel("assets/Models/Database.j3o");
            dataBaseStack.setLocalScale(0.6f);
            dataBaseStack.setName("DataBaseStackModel");

            Material material = new Material(app.getAssetManager(), "Common/MatDefs/Light/PBRLighting.j3md");
            /*metallness , max is 1*/
            material.setFloat("Metallic", 0.5f);
            Texture texture = app.getAssetManager()
                                 .loadTexture("assets/Textures/dataBaseTexture.jpg");
            material.setTexture("BaseColorMap", texture);
            material.setReceivesShadows(true);
            dataBaseStack.setMaterial(material);

            System.out.println("Asset-loading Thread: " + Thread.currentThread().getName());

            return dataBaseStack;
        } finally {
            /* Starts the JME task to attach this asset */
            ApplicationTasks.setAttachAssetTaskActive(taskExecutorsManager, true);
        }
    }

    @ExecuteOn(executors = {MonkeyTaskExecutor.class})
    public void attachAsset(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        /* Retrieves cached JME app */
        SimpleApplication application = MonkeyTasks.getApplication(taskExecutorsManager,
                                                                   MonkeyTaskExecutor.class);
        /* Retrieves cached Asset */
        Spatial dataBaseStack = Tasks.getWorkerTaskResult(taskExecutorsManager,
                TestMonkeyTaskBinder.AssetLoaderThread.class, "cacheAsset");

        application.getRootNode().attachChild(dataBaseStack);

        /* Applies the visitor pattern to retrieve a specific task */
        MonkeyWorkerTask task = MonkeyTasks.getWorkerTask(taskExecutorsManager,
                                                          MonkeyTaskExecutor.class, "attachAsset");
        System.out.println("JME Thread: " + Thread.currentThread().getName());
        System.out.println("Task Time per frame: " + task.getTimePerFrame());

        /* Disables this task */
        ApplicationTasks.setAttachAssetTaskActive(taskExecutorsManager, false);
        /* Activates the last task */
        ApplicationTasks.setCameraTaskActive(taskExecutorsManager, true);
    }

    @ExecuteOn(executors = {MonkeyTaskExecutor.class})
    public void setupCamera(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        /* Retrieves cached JME app */
        SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, MonkeyTaskExecutor.class);

         /* Retrieves cached Asset */
        Spatial dataBaseStack = (Spatial) Tasks.getWorkerTaskResult(taskExecutorsManager,
                TestMonkeyTaskBinder.AssetLoaderThread.class, "cacheAsset");

        /* -- Update JME Scene here -- */

        /* Disables this task */
        ApplicationTasks.setCameraTaskActive(taskExecutorsManager, false);
    }
```
- Helper classes: 
```java
protected static class ApplicationTasks {
  public static void setCameraTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
      Tasks.getWorkerTask(taskExecutorsManager,
              MonkeyTaskExecutor.class,
              "setupCamera").setActive(value);
  }

  public static void setAttachAssetTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
      Tasks.getWorkerTask(taskExecutorsManager,
              MonkeyTaskExecutor.class,
              "attachAsset").setActive(value);
  }

  public static void setSetupSceneTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
      Tasks.getWorkerTask(taskExecutorsManager,
              MonkeyTaskExecutor.class,
              "setupScene").setActive(value);
  }

  public static void setCacheAssetTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
      Tasks.getWorkerTask(taskExecutorsManager,
              TestMonkeyTaskBinder.AssetLoaderThread.class,
              "cacheAsset").setActive(value);
  }
}
```

## Features: 
- [x] Jector Dependencies are Functional Java Worker methods tasked in a TaskExecutor instance.
- [x] Multi-threaded async dependency tasking.
- [x] Single-threaded dependency tasking.
- [x] Non-threaded tasking.
- [x] Supports dependency arguments.
- [x] Supports dependency return objects to the caller (the parent task executor). 

## Featuring the DI pattern: 
1) Dependencies are defined by some Method objects as Tasks.
2) Dependencies created from Worker Method objects only.
3) Dependencies are bound to their Executors (threads, states, ...) interfaces via WorkerTasks (which enclose those low-level worker Methods).
4) Dependencies could be only instantiated from Worker Methods (annotated methods inside Worker classes).
5) A Worker interface is a marker interface for the Jector Framework.
6) A Worker implementation signifies a runtime environment for the DI actions.
7) A TaskBinder implementation has a Worker instance and is the injector object that creates WorkerTasks from Worker Methods and injects them into annotated TaskExecutors.
8) A TaskExecutor implementation is a wrapper to a collection of WorkerTasks to be executed, TaskExecutor could be a State, a Thread, a Server Container,...etc.
