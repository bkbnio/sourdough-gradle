# Sourdough Gradle

![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.bkbn.sourdough.root?style=for-the-badge)

## What is Sourdough Gradle?

Sourdough is a set of highly opinionated gradle plugins that aim to act as the starter for your Kotlin projects. It
offers extensive pre-configuration for a number of vital Gradle plugins. Below, you will find more info on the available
plugins, how to install them, and more insight into the variety of functionality provided.

1. [Available Plugins](#available-plugins)
    1. [Root](#root-plugin)
    2. [Library](#library-plugin)
    3. [Application](#application-plugin)
2. [Installation](#installation)
3. [Configuration](#configuration)
    1. [Root](#root-plugin-configuration)
    2. [Library](#library-plugin-configuration)
    3. [Application](#application-plugin-configuration)
4. [Under the Hood](#under-the-hood)

## Available Plugins

Sourdough is targeted at multi-module Kotlin (jvm-only) development. As such, there are specific plugins for the type of
module being built. At the very least, you _must_ apply the root plugin in your root `build.gradle.kts` as this plugin
hydrates a number of configurations that are used in other sourdough plugins.

### Root Plugin

This is where it all starts. This plugin is meant to be applied to the root `build.gradle.kts`. It preconfigures a
number of plugins, namely

- [Kotlin](https://kotlinlang.org) 1.6
- [Idea](https://docs.gradle.org/current/userguide/idea_plugin.html)
- [Detekt](https://detekt.github.io/detekt) 1.19.0
- [Test Logger](https://github.com/radarsh/gradle-test-logger-plugin) 3.1.0
- [Kover](https://github.com/Kotlin/kotlinx-kover) 0.4.4
- [Nexus Publishing](https://github.com/gradle-nexus/publish-plugin) 1.1.0

It also sets up two standard repositories, `mavenCentral` and `mavenLocal`. The former is useful for pulling down
publicly available dependencies, while the latter enables seamless local testing of libraries published to your local
maven repository (more on this in the [library](#library-plugin) section 😉).

### Library Plugin

The library plugin takes care of a large portion of the necessary setup for publishing consumable artifacts.

Out of the box, it installs the following plugins 

- `java` and `java-library`
- `maven-publish`
- `signing`

### Application Plugin

This doesn't do anything yet.  Don't use it. 

## Installation

Sourdough releases are published to the public gradle plugin repository. As such, there should be no additional
configuration necessary to get the baseline version of sourdough added to your project.

Sourdough can be added to your root `build.gradle.kts` as follows

```kotlin
plugins {
    id("io.bkbn.sourdough.root") version "0.0.4"
}
```

You should ensure that a `project.version` has been set for all project modules (including root).

This can be done easily with the `allprojects` block in your root `build.gradle.kts`

```kotlin
allprojects {
    group = "io.bkbn"
    version = "1.0.0"
}
```

### ⚠️ A note about sub-module plugins ⚠️

At the current moment, all sourdough plugins are published as a single JAR. As such, importing the root plugin
effectively adds all sourdough plugins to the classpath. This means that in submodules, when applying sourdough plugins,
**no version is necessary**.

This means that in a submodule that you wish to apply the library plugin, it would look like this

```kotlin
plugins {
    id("io.bkbn.sourdough.library") // No version here :)
}
```

## Configuration

In order to add a certain level of flexibility, each plugin comes with an extension class that can be used
to provide certain context to the plugin, allowing it to support a broad set of needs while remaining highly
opinionated.  

### Root Plugin Configuration

The root plugin is configured via the `SourdoughRootExtension`.  For convenience, a `sourdough` extension block is created.

```kotlin
sourdough {
    javaVersion = JavaVersion.VERSION_11
    compilerArgs = emptyList()
    sonatypeBaseUrl = "https://s01.oss.sonatype.org"
}
```

The values shown above are all the default options, so no setup is required out of the box, but each of these fields 
can be configured if necessary.

### Library Plugin Configuration

The library plugin is configured via the `SourdoughLibraryExtension`.  For convenience, a `sourdough` extension block is created.

Since no two sourdough plugins should ever be applied to the same project, overloading the extension name is not an issue.

```kotlin
sourdough {
    githubOrg = "REPLACE ME"
    githubRepo = "REPLACE ME"
    githubUsername = "REPLACE ME"
    githubToken = "REPLACE ME"
    libraryName = "REPLACE ME"
    libraryDescription = "REPLACE ME"
    licenseName = "REPLACE ME"
    licenseUrl = "REPLACE ME"
    developerId = "REPLACE ME"
    developerName = "REPLACE ME"
    developerEmail = "REPLACE ME"
}
```

These attributes are all used to automatically set up a number of library configurations such a library publication info and metadata

### Application Plugin Configuration

This doesn't exist... yet :)

## Under the Hood

TODO