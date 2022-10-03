The Sourdough Gradle Plugins are a collection of highly-opinionated, batteries-included gradle plugins to get you
started building delicious multi-module Kotlin projects.

At the moment, only JVM targeted plugins are supported. We hope to add multiplatform support in the future.

⚠️ DO NOT use the legacy multiplatform plugins (version `0.10.0` and lower). They will cause you more headache than they
are worth

# Plugins

## Root

The root plugin is intended to bootstrap the root `build.gradle.kts` file in your multi-module Gradle projects.

When installed, it can perform a number of configurations, namely

1. Bootstrapping your base maven repositories
2. Configure [Kover](https://github.com/Kotlin/kotlinx-kover) for integrated code coverage
3. Configure [Nexus](https://www.sonatype.com/products/nexus-repository) for library publishing

For more information on installation and configuration, please see the [root plugin docs](plugins/plugin_root.md)

⚠️ The root plugin is a pre-requisite for using any other sourdough-gradle plugins!

> [!COMMENT]
> An alert of type 'comment' using style 'callout' with default settings.

## JVM Application

## JVM Library