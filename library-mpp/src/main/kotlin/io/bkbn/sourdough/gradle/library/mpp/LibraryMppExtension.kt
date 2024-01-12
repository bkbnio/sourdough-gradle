package io.bkbn.sourdough.gradle.library.mpp

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class LibraryMppExtension {
  abstract val jvmTarget: Property<String>
  abstract val nodeJsVersion: Property<String>
  abstract val compilerArgs: ListProperty<String>
  abstract val githubOrg: Property<String>
  abstract val githubRepo: Property<String>
  abstract val githubActor: Property<String>
  abstract val githubToken: Property<String>
  abstract val libraryName: Property<String>
  abstract val libraryDescription: Property<String>
  abstract val licenseName: Property<String>
  abstract val licenseUrl: Property<String>
  abstract val developerId: Property<String>
  abstract val developerName: Property<String>
  abstract val developerEmail: Property<String>

  init {
    jvmTarget.convention("21")
    nodeJsVersion.convention("16.0.0")
    compilerArgs.convention(emptyList())
    githubActor.convention(System.getenv()["GITHUB_ACTOR"] ?: "Placeholder Github Actor")
    githubToken.convention(System.getenv()["GITHUB_TOKEN"] ?: "Placeholder Github Actor")
  }
}
