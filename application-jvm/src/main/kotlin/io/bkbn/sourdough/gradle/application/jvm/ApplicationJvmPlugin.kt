package io.bkbn.sourdough.gradle.application.jvm

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ApplicationJvmPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdoughApp", ApplicationJvmExtension::class.java)
    target.configureDetekt()
    target.configureApplication(ext)
    target.configureJava(ext)
    target.configureKotlin(ext)
    target.configureTesting()
  }

  private fun Project.configureDetekt() {
    plugins.withType(DetektPlugin::class.java) {
      extensions.configure(DetektExtension::class.java) {
        it.config = files("${rootProject.projectDir}/detekt.yml")
        it.buildUponDefaultConfig = true
      }
    }
  }

  private fun Project.configureApplication(ext: ApplicationJvmExtension) {
    plugins.withType(ApplicationPlugin::class.java) {
      extensions.configure(JavaApplication::class.java) {
        it.mainClass.set(ext.mainClassName)
      }
    }
  }

  private fun Project.configureKotlin(ext: ApplicationJvmExtension) {
    afterEvaluate {
      tasks.withType(KotlinCompile::class.java) {
        it.kotlinOptions {
          jvmTarget = ext.jvmTarget.get()
          freeCompilerArgs = freeCompilerArgs + ext.compilerArgs.get()
        }
      }
    }
  }

  private fun Project.configureJava(ext: ApplicationJvmExtension) {
    afterEvaluate {
      plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
          it.toolchain { jts ->
            jts.languageVersion.set(JavaLanguageVersion.of(ext.jvmTarget.get()))
          }
        }
      }
    }
  }


  private fun Project.configureTesting() {
    plugins.withType(TestLoggerPlugin::class.java) {
      extensions.configure(TestLoggerExtension::class.java) {
        it.theme = ThemeType.MOCHA
        it.logLevel = LogLevel.LIFECYCLE
        it.showExceptions = true
        it.showStackTraces = true
        it.showFullStackTraces = false
        it.showCauses = true
        it.slowThreshold = 2000
        it.showSummary = true
        it.showSimpleNames = false
        it.showPassed = true
        it.showSkipped = true
        it.showFailed = true
        it.showStandardStreams = false
        it.showPassedStandardStreams = true
        it.showSkippedStandardStreams = true
        it.showFailedStandardStreams = true
      }
    }
  }
}
