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

import com.avrsandbox.jector.core.command.MethodArguments;
import com.avrsandbox.jector.core.thread.AppThread;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutorsManager;
import com.avrsandbox.jector.monkey.core.work.MonkeyTaskExecutor;
import com.avrsandbox.jector.monkey.core.work.TaskExecutorListeners;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

/**
 * A JME Application tech demo to demonstrate the {@link MonkeyTaskExecutor} in action.
 *
 * @author pavl_g
 */
public final class TestMonkeyTaskBinder extends SimpleApplication implements TaskExecutorListeners {

    protected final AppThread assetLoaderThread = new AssetLoaderThread();
    protected MonkeyTaskExecutorsManager monkeyTaskBinder;
    protected final MonkeyTaskExecutor monkeyTaskExecutor = new MonkeyTaskExecutor("MonkeyExecutor");
    protected static final String JME_EXECUTOR = "JME_EXECUTOR";
    protected static final String ASSET_LOADER = "ASSET_LOADER";

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);

        TestMonkeyTaskBinder app = new TestMonkeyTaskBinder();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        monkeyTaskBinder = new MonkeyTaskExecutorsManager(new TestJectorInheritance(), TestMonkeyTaskBinder.this);

        monkeyTaskBinder.registerTaskExecutor(ASSET_LOADER, assetLoaderThread);
        monkeyTaskBinder.registerTaskExecutor(JME_EXECUTOR, monkeyTaskExecutor);
        monkeyTaskBinder.registerTaskExecutor(JME_EXECUTOR, monkeyTaskExecutor);

        assetLoaderThread.setActive(true);
        monkeyTaskExecutor.setActive(true);
        monkeyTaskExecutor.setTaskExecutorListeners(this);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void onExecutorInitialized(Application app) {
        monkeyTaskBinder.bind(new MethodArguments());
        monkeyTaskBinder.getTaskExecutors()
                .get(ASSET_LOADER)
                .getTasks()
                .get("setupSky")
                .setActive(true);
    }

    @Override
    public void onStartExecutorService() {

    }

    @Override
    public void onStopExecutorService() {

    }

    @Override
    public void onExecutorCleanUp(Application app) {

    }

    @Override
    public void onExecutorEnabled(Application app) {

    }

    @Override
    public void onExecutorDisabled(Application app) {

    }

    /**
     * An internal daemon thread for async heavy duty asset loading.
     */
    public static class AssetLoaderThread extends AppThread {
        public AssetLoaderThread() {
            super(AssetLoaderThread.class.getName());
            setDaemon(true);
        }
    }
}
