package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.JavaVersion

open class SourdoughRootExtension {
  var javaVersion: JavaVersion = JavaVersion.VERSION_11
  var compilerArgs: List<String> = emptyList()
  var sonatypeBaseUrl: String = "https://s01.oss.sonatype.org"
}
