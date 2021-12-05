package io.bkbn.sourdough.gradle.core.extension

import org.gradle.api.provider.Property

abstract class SourdoughLibraryExtension {
  abstract val githubOrg: Property<String>
  abstract val githubRepo: Property<String>
  abstract val githubUsername: Property<String>
  abstract val githubToken: Property<String>
  abstract val libraryName: Property<String>
  abstract val libraryDescription: Property<String>
  abstract val licenseName: Property<String>
  abstract val licenseUrl: Property<String>
  abstract val developerId: Property<String>
  abstract val developerName: Property<String>
  abstract val developerEmail: Property<String>
}
