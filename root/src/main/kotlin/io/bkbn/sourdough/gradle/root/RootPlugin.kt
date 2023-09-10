package io.bkbn.sourdough.gradle.root

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdoughRoot", RootExtension::class.java)
    target.configureBaseRepositories()
    target.configureKover()
    target.configureNexus(ext)
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

  private fun Project.configureNexus(ext: RootExtension) {
    plugins.withType(NexusPublishPlugin::class.java) {
      extensions.configure(NexusPublishExtension::class.java) { npe ->
        npe.repositories { nrc ->
          nrc.sonatype { nr ->
            nr.nexusUrl.set(ext.sonatypeNexusUrl)
            nr.snapshotRepositoryUrl.set(ext.sonatypeSnapshotRepositoryUrl)
          }
        }
      }
    }
  }
}
