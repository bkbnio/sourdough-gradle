The JVM application plugin is intended for use in child modules meant to be deployed as executable jars.

# Installation

Like all sourdough gradle plugins, the JVM application plugin is published to the Gradle plugin repository, and can be
installed to your project simply be declaring it in the `plugins` block

```kotlin
plugins {
    // ...
    id("io.bkbn.sourdough.application.jvm") version "0.11.0"
}
```

{% hint style="info" %}
We recommend installing the plugin in the root `build.gradle.kts`, suffixed with`apply false`, and then declaring in your
application child modules.  This keeps the plugin version consistent across all children
{% endhint %}

```kotlin
// ./build.gradle.kts
plugins {
    // ...
    id("io.bkbn.sourdough.root") version "0.11.0"
    id("io.bkbn.sourdough.application.jvm") version "0.11.0" apply false
}

// ./app/build.gradle.kts
plugins {
    // ...
    id("io.bkbn.sourdough.application.jvm")
}
```

# Configuration

TODO

# Extension

TODO