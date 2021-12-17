package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.JavaVersion

open class SourdoughRootExtension {
  var toolChainJavaVersion: JavaVersion = JavaVersion.VERSION_11
  var jvmTarget: String = toolChainJavaVersion.majorVersion
  var compilerArgs: List<String> = emptyList()
  var sonatypeBaseUrl: String = "https://s01.oss.sonatype.org"
}
