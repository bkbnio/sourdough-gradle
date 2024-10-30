package io.bkbn.sourdough.gradle.root

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdoughRoot", RootExtension::class.java)
    target.configureBaseRepositories()
    target.configureKover()
  }

  private fun Project.configureBaseRepositories() {
    allprojects {
      it.repositories.apply {
        mavenCentral()
        mavenLocal()
      }
    }
  }

  private fun Project.configureKover() {
    plugins.withType(KoverGradlePlugin::class.java) {
      dependencies.apply {
        // TODO Doesn't work
        // subprojects.forEach { kover(it) }
      }
    }
  }
}
