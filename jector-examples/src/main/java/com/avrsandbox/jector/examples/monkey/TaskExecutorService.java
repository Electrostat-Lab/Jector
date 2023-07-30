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

package com.avrsandbox.jector.examples.monkey;

import com.avrsandbox.jector.core.command.ExecuteOn;
import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import com.avrsandbox.jector.core.work.Worker;
import com.avrsandbox.jector.monkey.core.work.MonkeyWorkerTask;
import com.avrsandbox.jector.monkey.util.MonkeyTasks;
import com.avrsandbox.jector.util.Tasks;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 * Example for AssetLoading (Spatial/Sky) using a daemon thread.
 *
 * @author pavl_g
 */
public class TaskExecutorService implements Worker {

    @ExecuteOn(executors = {TestMonkeyTaskExecutorManager.ASSET_LOADER})
    public Geometry setupSky(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        try {
            SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR);

            Geometry sky = (Geometry) SkyFactory.createSky(app.getAssetManager(),
                    app.getAssetManager().loadTexture("assets/Textures/sky.jpg"), Vector3f.UNIT_XYZ, SkyFactory.EnvMapType.EquirectMap);
            sky.setLocalScale(0.5f);
            sky.getMaterial().getAdditionalRenderState().setDepthFunc(RenderState.TestFunction.LessOrEqual);

            return sky;
        } finally {
            ApplicationTasks.setSetupSceneTaskActive(taskExecutorsManager, true);
        }
    }

    @ExecuteOn(executors = {TestMonkeyTaskExecutorManager.JME_EXECUTOR})
    public void setupScene(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR);
        Geometry sky = Tasks.getWorkerTaskResult(taskExecutorsManager, TestMonkeyTaskExecutorManager.ASSET_LOADER, "setupSky");

        app.getRootNode().attachChild(sky);

        AmbientLight ambientLight=new AmbientLight();
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

    @ExecuteOn(executors = {TestMonkeyTaskExecutorManager.ASSET_LOADER})
    public Spatial cacheAsset(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        try {
            SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR);
            Spatial dataBaseStack = app.getAssetManager().loadModel("assets/Models/Database.j3o");
            dataBaseStack.setLocalScale(0.6f);
            dataBaseStack.setName("DataBaseStackModel");

            Material material = new Material(app.getAssetManager(), "Common/MatDefs/Light/PBRLighting.j3md");
            /*metalness , max is 1*/
            material.setFloat("Metallic", 0.5f);
            Texture texture = app.getAssetManager().loadTexture("assets/Textures/dataBaseTexture.jpg");
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

    @ExecuteOn(executors = {TestMonkeyTaskExecutorManager.JME_EXECUTOR})
    public void attachAsset(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {
        SimpleApplication application = MonkeyTasks.getApplication(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR);
        Spatial dataBaseStack = Tasks.getWorkerTaskResult(taskExecutorsManager,
                TestMonkeyTaskExecutorManager.ASSET_LOADER, "cacheAsset");
        application.getRootNode().attachChild(dataBaseStack);

        MonkeyWorkerTask task = MonkeyTasks.getWorkerTask(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR, "attachAsset");

        System.out.println("JME Thread: " + Thread.currentThread().getName());
        System.out.println("Task Time per frame: " + task.getTimePerFrame());

        /* Disables this task */
        ApplicationTasks.setAttachAssetTaskActive(taskExecutorsManager, false);
        /* Activates the last task */
        ApplicationTasks.setCameraTaskActive(taskExecutorsManager, true);
    }

    @ExecuteOn(executors = {TestMonkeyTaskExecutorManager.JME_EXECUTOR})
    public void setupCamera(MethodArguments args, TaskExecutorsManager taskExecutorsManager) {

        SimpleApplication app = MonkeyTasks.getApplication(taskExecutorsManager, TestMonkeyTaskExecutorManager.JME_EXECUTOR);

        Spatial dataBaseStack = Tasks.getWorkerTaskResult(taskExecutorsManager,
                TestMonkeyTaskExecutorManager.ASSET_LOADER, "cacheAsset");

        app.getCamera().setFrustumNear(0.7f);
        ChaseCamera chaseCamera = new ChaseCamera(app.getCamera(), dataBaseStack, app.getInputManager());
        chaseCamera.setDragToRotate(true);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setDefaultDistance(-30f);
        chaseCamera.setMaxDistance(-10f);
        chaseCamera.setMinDistance(-5f);
        chaseCamera.setDefaultVerticalRotation(-FastMath.QUARTER_PI / 2);
        chaseCamera.setDefaultHorizontalRotation(-FastMath.HALF_PI);
        chaseCamera.setHideCursorOnRotate(true);

        /* Disables this task */
        taskExecutorsManager.unregisterTaskExecutor(TestMonkeyTaskExecutorManager.ASSET_LOADER);
        taskExecutorsManager.unregisterTaskExecutor(TestMonkeyTaskExecutorManager.JME_EXECUTOR);
    }

    protected static class ApplicationTasks {
        public static void setCameraTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
            Tasks.getWorkerTask(taskExecutorsManager,
                    TestMonkeyTaskExecutorManager.JME_EXECUTOR,
                    "setupCamera").setActive(value);
        }

        public static void setAttachAssetTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
            Tasks.getWorkerTask(taskExecutorsManager,
                    TestMonkeyTaskExecutorManager.JME_EXECUTOR,
                    "attachAsset").setActive(value);
        }

        public static void setSetupSceneTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
            Tasks.getWorkerTask(taskExecutorsManager,
                    TestMonkeyTaskExecutorManager.JME_EXECUTOR,
                    "setupScene").setActive(value);
        }

        public static void setCacheAssetTaskActive(TaskExecutorsManager taskExecutorsManager, boolean value) {
            Tasks.getWorkerTask(taskExecutorsManager,
                    TestMonkeyTaskExecutorManager.ASSET_LOADER,
                    "cacheAsset").setActive(value);
        }
    }
}
