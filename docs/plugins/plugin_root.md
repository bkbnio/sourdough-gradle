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
  id("io.bkbn.sourdough.library.jvm") version "0.10.0" apply false
  id("io.bkbn.sourdough.application.jvm") version "0.10.0" apply false
  id("io.bkbn.sourdough.root") version "0.10.0"
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
  id("org.jetbrains.kotlinx.kover") version "0.6.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}
```