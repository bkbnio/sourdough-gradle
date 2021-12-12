package io.bkbn.sourdough.gradle.core.extension

open class SourdoughLibraryExtension {
  lateinit var githubOrg: String
  lateinit var githubRepo: String
  lateinit var githubUsername: String
  lateinit var githubToken: String
  lateinit var libraryName: String
  lateinit var libraryDescription: String
  lateinit var licenseName: String
  lateinit var licenseUrl: String
  lateinit var developerId: String
  lateinit var developerName: String
  lateinit var developerEmail: String
}
