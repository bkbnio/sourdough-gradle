The JVM Library plugin is intended for use in child modules meant to be published as artifacts in a jar repository such
as Maven Central.

# Installation

Like all sourdough gradle plugins, the JVM library plugin is published to the Gradle plugin repository, and can be
installed to your project simply be declaring it in the `plugins` block

```kotlin
plugins {
    // ...
    id("io.bkbn.sourdough.library.jvm") version "0.11.0"
}
```

{% hint style="info" %}
We recommend installing the plugin in the root `build.gradle.kts`, suffixed with`apply false`, and then declaring in
your library child modules. This keeps the plugin version consistent across all children
{% endhint %}

```kotlin
// ./build.gradle.kts
plugins {
    // ...
    id("io.bkbn.sourdough.root") version "0.11.0"
    id("io.bkbn.sourdough.library.jvm") version "0.11.0" apply false
}

// ./app/build.gradle.kts
plugins {
    // ...
    id("io.bkbn.sourdough.library.jvm")
}
```

# Configuration

TODO

# Extension

TODO