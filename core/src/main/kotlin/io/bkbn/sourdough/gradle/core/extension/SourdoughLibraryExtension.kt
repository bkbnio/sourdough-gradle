package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.provider.Property

// TODO Move some of these to parent
abstract class SourdoughLibraryExtension {
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
    githubActor.convention(System.getenv()["GITHUB_ACTOR"] ?: "Placeholder Github Actor")
    githubToken.convention(System.getenv()["GITHUB_TOKEN"] ?: "Placeholder Github Actor")
  }
}
