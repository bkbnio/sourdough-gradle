The Sourdough Gradle Plugins are a collection of highly-opinionated, batteries-included gradle plugins to get you
started building delicious multi-module Kotlin projects.

{% hint style="danger" %}
DO NOT use the legacy multiplatform plugins (version `0.10.0` and lower). They will cause you more headache than they
are worth
{% endhint %}

# Plugins

## Root

The root plugin is intended to bootstrap the root `build.gradle.kts` file in your multi-module Gradle projects.

When installed, it can perform a number of configurations

1. Bootstrapping your base maven repositories
2. Configure [Kover](https://github.com/Kotlin/kotlinx-kover) for integrated code coverage
3. Configure [Nexus](https://www.sonatype.com/products/nexus-repository) for library publishing

For more information on installation and configuration, please see the [root plugin docs](plugins/plugin_root.md)

{% hint style="warning" %}
The root plugin is a pre-requisite for using any other sourdough-gradle plugins!
{% endhint %}

## JVM Application

The JVM Application plugin is intended for bootstrapping modules where the intended purpose is a deployed application.

When installed to a child module, it can perform a number of configurations

1. Configure [Detekt](https://detekt.dev)
2. Configure gradle JVM settings
3. Configure [Kover](https://github.com/Kotlin/kotlinx-kover) so module code coverage is fed back to the root module
4. Configure [TestLogger](https://github.com/radarsh/gradle-test-logger-plugin) for improved test output

For more information on installing and configuring, please see
the [jvm application plugin docs](plugins/plugin_application_jvm.md)

## JVM Library

The JVM Library plugin is intended for bootstrapping modules where the intended purpose is publishing an artifact to a
maven repository. It is focused on publishing to [Maven Central](https://search.maven.org/) but all repositories are
supported.

When installed to a child module, this plugin can perform a number of configurations

1. Configure [Detekt](https://detekt.dev)
2. Configure gradle JVM settings
3. Configure [Kover](https://github.com/Kotlin/kotlinx-kover) so module code coverage is fed back to the root module
4. Configure [TestLogger](https://github.com/radarsh/gradle-test-logger-plugin) for improved test output
5. Configure Maven publishing
6. Configure Sonatype Signing

For more information on installing and configuring, please see
the [library jvm plugin docs](plugins/plugin_library_jvm.md)

## Multiplatform Library

The Multiplatform Library plugin is intended for bootstrapping modules where the intended purpose is publishing
multiplatform artifacts to a maven repository. It is focused on publishing to [Maven Central](https://search.maven.org/)
but all repositories are supported.

When installed to a child module, this plugin can perform a number of configurations

1. Configure Maven publishing
2. Configure Sonatype Signing