package io.bkbn.sourdough.gradle.core

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.bkbn.sourdough.gradle.core.extension.SourdoughRootExtension
import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.api.KoverExtension
import kotlinx.kover.tasks.KoverCollectingTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.buildscript
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val extension = target.extensions.create<SourdoughRootExtension>("sourdough")
    target.configureRepositories()
    target.configureIdea()
    target.configureDetekt()
    target.configureKotlin(extension)
    target.configureTesting()
    target.configureDokka()
    target.configureKover()
    target.configureNexus(extension)
  }

  private fun Project.configureDetekt() {
    subprojects {
      apply(plugin = "io.gitlab.arturbosch.detekt")
      configure<DetektExtension> {
        toolVersion = "1.19.0"
        config = files("${rootProject.projectDir}/detekt.yml")
        buildUponDefaultConfig = true
      }
    }
  }

  private fun Project.configureKotlin(ext: SourdoughRootExtension) {
    subprojects {
      apply(plugin = "org.jetbrains.kotlin.jvm")
      afterEvaluate {
        configure<JavaPluginExtension> {
          withSourcesJar()
          withJavadocJar()
          toolchain {
            languageVersion.set(ext.toolChainJavaVersion)
            vendor.set(JvmVendorSpec.ADOPTOPENJDK)
          }
        }
        tasks.withType<KotlinCompile> {
          sourceCompatibility = ext.jvmTarget.get()
          kotlinOptions {
            jvmTarget = ext.jvmTarget.get()
            freeCompilerArgs = freeCompilerArgs + ext.compilerArgs.get()
          }
        }
      }
    }
  }

  private fun Project.configureRepositories() {
    allprojects {
      repositories {
        mavenCentral()
        mavenLocal()
      }
    }
  }

  private fun Project.configureIdea() {
    apply(plugin = "idea")
  }

  private fun Project.configureTesting() {
    subprojects {
      apply(plugin = "com.adarshr.test-logger")
      tasks.withType<Test> {
        useJUnitPlatform()
      }
      @Suppress("MagicNumber")
      configure<TestLoggerExtension> {
        theme = ThemeType.MOCHA
        setLogLevel("lifecycle")
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        showCauses = true
        slowThreshold = 2000
        showSummary = true
        showSimpleNames = false
        showPassed = true
        showSkipped = true
        showFailed = true
        showStandardStreams = false
        showPassedStandardStreams = true
        showSkippedStandardStreams = true
        showFailedStandardStreams = true
      }
    }
  }

  private fun Project.configureDokka() {
    apply(plugin = "org.jetbrains.dokka")
    beforeEvaluate {
      buildscript {
        dependencies {
          classpath("org.jetbrains.dokka:versioning-plugin:1.6.0")
        }
      }
    }
    afterEvaluate {
      tasks.withType(DokkaMultiModuleTask::class.java) {
        val version = version.toString()
        outputDirectory.set(rootDir.resolve("dokka/$version"))
        dependencies {
          addProvider("dokkaPlugin", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
        }
        pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
          setVersion(version)
          olderVersionsDir = rootDir.resolve("dokka")
        }
        finalizedBy("generateDokkaHomePage")
      }
    }
    tasks.register("generateDokkaHomePage") {
      val version = version.toString()
      val dokkaDir = rootDir.resolve("dokka")
      if (!dokkaDir.exists()) {
        dokkaDir.mkdir()
      }
      val index = rootDir.resolve("dokka/index.html")
      if (!index.exists()) {
        index.createNewFile()
      }
      index.writeText("<meta http-equiv=\"refresh\" content=\"0; url=./$version\" />\n")
    }
  }

  private fun Project.configureKover() {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    configure<KoverExtension> {
      isEnabled = true
      coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
      jacocoEngineVersion.set("0.8.7")
      generateReportOnCheck.set(true)
    }
    afterEvaluate {
      val kcr = tasks.getByName("koverCollectReports") as KoverCollectingTask
      kcr.apply {
        outputDir.set(layout.buildDirectory.dir("kover-report"))
      }
    }
  }

  private fun Project.configureNexus(ext: SourdoughRootExtension) {
    apply(plugin = "io.github.gradle-nexus.publish-plugin")
    afterEvaluate {
      configure<NexusPublishExtension> {
        repositories {
          sonatype {
            nexusUrl.set(ext.sonatypeNexusUrl)
            snapshotRepositoryUrl.set(ext.sonatypeSnapshotRepositoryUrl)
          }
        }
      }
    }
  }
}
