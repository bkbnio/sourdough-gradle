package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.JavaVersion
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import java.net.URI

abstract class SourdoughRootExtension {
  abstract val toolChainJavaVersion: Property<JavaLanguageVersion>
  abstract val jvmTarget: Property<String>
  abstract val compilerArgs: ListProperty<String>
  abstract val sonatypeNexusUrl: Property<URI>
  abstract val sonatypeSnapshotRepositoryUrl: Property<URI>
  abstract val documentationFolder: Property<String>

  init {
    toolChainJavaVersion.convention(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
    jvmTarget.convention(JavaVersion.VERSION_11.majorVersion)
    compilerArgs.convention(emptyList())
    sonatypeNexusUrl.convention(URI("https://s01.oss.sonatype.org/service/local/"))
    sonatypeSnapshotRepositoryUrl.convention(URI("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    documentationFolder.convention("docs")
  }
}
