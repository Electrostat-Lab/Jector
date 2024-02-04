# <img src="https://github.com/Software-Hardware-Codesign/Jector/assets/60224159/a7989a90-9c00-483c-9ffd-166f8a50b21c" width=60 length=60/> Jector [![Codacy Badge](https://app.codacy.com/project/badge/Grade/3fe7dc8b13ec42a7a15e4c851d90f47e)](https://app.codacy.com/gh/Software-Hardware-Codesign/Jector/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) [![](https://img.shields.io/badge/Jector_Framework-latest_version-red)](https://github.com/Software-Hardware-Codesign/Jector/releases/tag/1.0.0-pre-alpha)
[![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-test.yml/badge.svg)]() [![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-deploy.yml/badge.svg)]()

An advanced DI framework for JVM and Android applications based on the Java Reflection API with a specialized implementation for jMonkeyEngine Applications.

## Software specification: 
| *_Item_* | *Description* |
|-----------|-----------|
| _Problem_ | The problem is doing multi-threaded/concurrent code in games is overwhelming and will require you to use synchronization or locks at some point in your application which might disrupt the architecture in your final software product. |
| _Jector Approach_ | Jector solves this by making good use of the DI pattern to bind your proposed function stack frame as a `WorkerTask` to a `TaskExecutor` instance, a TaskExecutor instance could be a Thread, a Game State, an Android Task,...etc. |
| _DI Mechanism_ | DI stands for Dependency Injection, as the name implies, it consists of 3 objects, a dependency object, a dependent object, and an injector object, the dependency object contains the delegation code that is utilized by a dependent object, the injector object role is to pass the dependency object to the actual dependent to complete its job. | 
| _DI in Jector_ | DI in Jector works by injecting a method as a WorkerTask (dependency) into a TaskExecutor (dependent object) using a TaskExecutorManager (injector object). 
| _Jector in practice_ | Loading game assets asynchronously is now easier, by enabling some tasks to be executed at some point on their respective threads. |
| _Synergism with virtual threads_ | WIP |

## Building:
```bash
┌─[pavl-machine@pavl-machine]─[/home/twisted/GradleProjects/Jector]
└──╼ $./gradlew :jector:build && \
      ./gradlew :jector:generateSourcesJar && \
      ./gradlew :jector:generateJavadocJar && \
      ./gradlew :jector-monkey build && \
      ./gradlew :jector-monkey generateSourcesJar && \
      ./gradlew :jector-monkey generateJavadocJar
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
    implementation "io.github.software-hardware-codesign:jector:1.0.0-pre-alpha"
    implementation "io.github.software-hardware-codesign:jector-monkey:1.0.0-pre-alpha"
    implementation "org.jmonkeyengine:jme3-core:3.6.1-stable"
    implementation "org.jmonkeyengine:jme3-desktop:3.6.1-stable"
    implementation "org.jmonkeyengine:jme3-lwjgl3:3.6.1-stable"
}
```

## Features: 
- [x] Jector Dependencies are Functional Java Worker methods tasked in a TaskExecutor instance.
- [x] Multi-threaded async dependency tasking.
- [x] Single-threaded dependency tasking.
- [x] Non-threaded tasking.
- [x] Supports dependency arguments.
- [x] Supports dependency return objects to the caller (the parent task executor).
- [x] Supports OO Polymorphism over the Worker classes (need to add all the workers statically to the TaskExecutorManager).
- [x] Supports Android and jMonkeyEngine Applications.

## Featuring the DI pattern: 
1) Dependencies are defined by some Method objects as `WorkerTasks`.
2) Dependencies created from Worker Method objects only.
3) Dependencies are bound to their Executors (threads, states, ...) interfaces via WorkerTasks (which enclose those low-level worker Methods).
4) Dependencies could be only instantiated from Worker Methods (annotated methods inside Worker classes).
5) A `Worker` interface is a marker interface for the Jector Framework.
6) A `Worker` implementation signifies a runtime environment for the DI actions.
7) A `TaskExecutorManager` implementation has a Worker instance and is the injector object that creates WorkerTasks from Worker Methods and injects them into annotated TaskExecutors.
8) A `TaskExecutor` implementation is a wrapper to a collection of WorkerTasks to be executed, TaskExecutor could be a State, a Thread, a Server Container,...etc.
