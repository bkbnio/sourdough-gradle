package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.JavaVersion
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class SourdoughRootExtension {
  abstract val javaVersion: Property<JavaVersion>
  abstract val compilerArgs: ListProperty<String>
  abstract val sonatypeBaseUrl: Property<String>
}
