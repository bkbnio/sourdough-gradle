package io.bkbn.sourdough.gradle.application.jvm

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class ApplicationJvmExtension {
  abstract val jvmTarget: Property<String>
  abstract val compilerArgs: ListProperty<String>
  abstract val mainClassName: Property<String>

  init {
    jvmTarget.convention("21")
    compilerArgs.convention(emptyList())
  }
}
