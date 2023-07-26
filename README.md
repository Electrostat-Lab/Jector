# <img src="https://github.com/Software-Hardware-Codesign/Jector/assets/60224159/dcaa688b-ddc1-4534-84e7-79f7da91f1a6" width=60 length=60/> Jector [![Codacy Badge](https://app.codacy.com/project/badge/Grade/3fe7dc8b13ec42a7a15e4c851d90f47e)](https://app.codacy.com/gh/Software-Hardware-Codesign/Jector/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
[![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-test.yml/badge.svg)]() [![](https://github.com/Software-Hardware-Codesign/jector/actions/workflows/build-deploy.yml/badge.svg)]()

An advanced DI framework for JVM and Android applications based on the Java Reflection API.

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
