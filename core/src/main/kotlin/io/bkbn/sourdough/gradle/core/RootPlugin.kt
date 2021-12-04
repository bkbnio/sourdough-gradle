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
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType

class RootPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.configureRepositories()
    target.configureIdea()
    target.configureDetekt()
    target.configureKotlin()
    target.configureTesting()
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
}
