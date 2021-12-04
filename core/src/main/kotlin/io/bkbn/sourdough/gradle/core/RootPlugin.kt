package io.bkbn.sourdough.gradle.core

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.buildscript
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.configureRepositories()
    target.configureIdea()
    target.configureDetekt()
    target.configureKotlin()
    target.configureTesting()
    target.configureDokka()
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

  private fun Project.configureKotlin() {
    subprojects {
      apply(plugin = "org.jetbrains.kotlin.jvm")
      configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()

        toolchain {
          languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
          vendor.set(JvmVendorSpec.ADOPTOPENJDK)
        }
      }
      // TODO Why doesnt this work?
//      configure<KotlinCompile> {
//        sourceCompatibility = "11"
//        kotlinOptions {
//          jvmTarget = "11"
//          freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
//        }
//      }
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
      val dmmt = tasks.getByName("dokkaHtmlMultiModule") as DokkaMultiModuleTask
      dmmt.apply {
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
      val index = rootDir.resolve("dokka/index.html")
      index.writeText("<meta http-equiv=\"refresh\" content=\"0; url=./$version\" />\n")
    }
  }
}
