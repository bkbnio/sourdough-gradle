The root plugin sits at the root of your project, and helps to keep your root `build.gradle.kts` nice and clean

# Installation

Like all sourdough gradle plugins, the root plugin is published to the Gradle plugin repository, and can be added to
your project by simply declaring it in the `plugins` block

```kotlin
plugins {
    // ...
    id("io.bkbn.sourdough.root") version "0.11.0"
}
```

{% hint style="warning" %}
Sourdough plugins follow a _reactive_ philosophy, meaning they do not install any dependencies for you.
{% endhint %}

As such, you will need to make sure to install all of the plugins you want. Once you add a plugin to the classpath,
sourdough will pick it up and perform any auto-configurations that have been declared for that plugin.

{% hint style="info" %}
We recommended installing _all_ plugins on your root `build.gradle.kts` and setting `apply false` for plugins you only
wish to install on child modules. This keeps plugin versions consistent across all modules.
{% endhint %}

An example `plugins` block of your root `build.gradle.kts` should you choose to go full sourdough would look something
like this

```kotlin
plugins {
    kotlin("jvm") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
    id("io.bkbn.sourdough.library.jvm") version "0.11.0" apply false
    id("io.bkbn.sourdough.application.jvm") version "0.11.0" apply false
    id("io.bkbn.sourdough.root") version "0.11.0"
    id("com.github.jakemarsden.git-hooks") version "0.0.2"
    id("org.jetbrains.kotlinx.kover") version "0.6.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}
```

# Configuration

As mentioned above, sourdough follows a reactive strategy to plugin configuration. As such, only plugins added to the
classpath manually will be considered for configuration.

When installed, root plugin scans for two additional plugins, `org.jetbrains.kotlinx.kover`
and `io.github.gradle-nexus.publish-plugin`

## Kover

All you need to do in order for sourdough to configure [Kover](https://github.com/Kotlin/kotlinx-kover) is to install
the kover plugin `org.jetbrains.kotlinx.kover`. Once you do so, sourdough will set up a kover project configuration,
enable the Jacoco coverage engine, and set up a centralized folder for collection reports.

The output folder is `./build/reports/kover/merged` and the output is generated in both HTML and XML formats.

## Nexus

Publishing to Nexus is kind of a pain in the ass. Sourdough abstracts away nearly all of this, and does so as long
as you add the `io.github.gradle-nexus.publish-plugin` plugin to your classpath. If you do not wish to publish to Nexus,
simply do not install this plugin.

# Extension

If you need to override the default sourdough configuration, you can do so through the `sourdoughRoot` extension block

```kotlin
sourdoughRoot {
    // ...
}
```

Currently, the only configurable values are

- `sonatypeNexusUrl` for setting the production Sonatype url (defaults to `https://s01.oss.sonatype.org/service/local/`)
- `sonatypeSnapshotRepositoryUrl` for setting the snapshot Sonatype url (defaults
  to `https://s01.oss.sonatype.org/content/repositories/snapshots/`)