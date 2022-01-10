package io.bkbn.sourdough.gradle.root

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import kotlinx.kover.KoverPlugin
import kotlinx.kover.api.CoverageEngine
import kotlinx.kover.api.KoverExtension
import kotlinx.kover.tasks.KoverCollectingProjectsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdough", RootExtension::class.java)
    target.configureBaseRepositories()
    target.configureDokka(ext)
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

  private fun Project.configureDokka(ext: RootExtension) {
    plugins.withType(DokkaPlugin::class.java) {
      beforeEvaluate {
        it.buildscript.apply {
          dependencies.apply {
            this.addProvider("classpath", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
          }
        }
      }
      // TODO This smells... must be a better way to get versions
      afterEvaluate {
        tasks.withType(DokkaMultiModuleTask::class.java) {
          it.outputDirectory.set(rootDir.resolve("${ext.documentationFolder.get()}/${rootProject.version}"))
          dependencies.apply {
            addProvider("dokkaPlugin", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
          }
          it.pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
            setVersion(project.version)
            olderVersionsDir = rootDir.resolve(ext.documentationFolder.get())
          }
          val projectMd = rootDir.resolve("Project.md")
          if (projectMd.exists()) {
            it.includes.from("Project.md")
          }
          it.finalizedBy("generateDokkaHomePage")
        }
        tasks.register("generateDokkaHomePage") {
          it.doLast {
            val version = version.toString()
            val dokkaDir = rootDir.resolve(ext.documentationFolder.get())
            if (!dokkaDir.exists()) {
              dokkaDir.mkdir()
            }
            val index = rootDir.resolve("${ext.documentationFolder.get()}/index.html")
            if (!index.exists()) {
              index.createNewFile()
            }
            index.writeText("<meta http-equiv=\"refresh\" content=\"0; url=./$version\" />\n")
          }
        }
      }
    }
  }

  private fun Project.configureKover() {
    plugins.withType(KoverPlugin::class.java) {
      extensions.configure(KoverExtension::class.java) {
        it.coverageEngine.set(CoverageEngine.JACOCO)
        it.jacocoEngineVersion.set("0.8.7")
        it.generateReportOnCheck.set(true)
      }
      tasks.withType(KoverCollectingProjectsTask::class.java).configureEach {
        it.outputDir.set(layout.buildDirectory.dir("kover-report"))
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
