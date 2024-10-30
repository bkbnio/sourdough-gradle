package io.bkbn.sourdough.gradle.library.jvm

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishBasePlugin
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

class LibraryJvmPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdoughLibrary", LibraryJvmExtension::class.java)
    target.configureDetekt()
    target.configureJava(ext)
    target.configureKotlin(ext)
    target.configureKover()
    target.configureTesting()
    target.configurePublishing()
    target.configureSigning()
  }

  private fun Project.configureDetekt() {
    plugins.withType(DetektPlugin::class.java) {
      extensions.configure(DetektExtension::class.java) {
        it.config = files("${rootProject.projectDir}/detekt.yml")
        it.buildUponDefaultConfig = true
      }
    }
  }

  private fun Project.configureJava(ext: LibraryJvmExtension) {
    afterEvaluate {
      plugins.withType(JavaLibraryPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
          it.toolchain { jts ->
            jts.languageVersion.set(JavaLanguageVersion.of(ext.jvmTarget.get()))
          }
        }
      }
    }
  }

  private fun Project.configureKotlin(ext: LibraryJvmExtension) {
    afterEvaluate {
      tasks.withType(KotlinCompile::class.java) {
        it.kotlinOptions {
          jvmTarget = ext.jvmTarget.get()
          freeCompilerArgs = freeCompilerArgs + ext.compilerArgs.get()
        }
      }
    }
  }

  private fun Project.configureKover() {
    plugins.withType(KoverGradlePlugin::class.java) {
      extensions.configure(KoverProjectExtension::class.java) {
        it.useJacoco()
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

  private fun Project.configurePublishing() {
    afterEvaluate {
      extensions.findByType(LibraryJvmExtension::class.java)?.let { ext ->
        plugins.withType(MavenPublishBasePlugin::class.java) {
          extensions.configure(MavenPublishBaseExtension::class.java) { mpbe ->
            mpbe.publishToMavenCentral(ext.sonatypeHost.get())
            mpbe.signAllPublications()
            mpbe.coordinates(
              groupId = project.group.toString(),
              artifactId = project.name.lowercase(Locale.getDefault()),
              version = version.toString()
            )

            mpbe.pom { mpom ->
              mpom.name.set(ext.libraryName)
              mpom.description.set(ext.libraryDescription)
              mpom.url.set("https://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}")
              if (ext.licenseName.isPresent && ext.licenseUrl.isPresent) {
                mpom.licenses { mpls ->
                  mpls.license { mpl ->
                    mpl.name.set(ext.licenseName)
                    mpl.url.set(ext.licenseUrl)
                  }
                }
              }
              mpom.developers { mpds ->
                mpds.developer { mpd ->
                  mpd.id.set(ext.developerId)
                  mpd.name.set(ext.developerName)
                  mpd.email.set(ext.developerEmail)
                }
              }

              mpom.scm { mps ->
                mps.connection.set("scm:git:git://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
                mps.developerConnection.set("scm:git:ssh://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
                mps.url.set("https://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
              }
            }
          }
        }
      }
    }
  }

  private fun Project.configureSigning() {
    afterEvaluate {
      plugins.withType(SigningPlugin::class.java) {
        if (((project.findProperty("release") as? String)?.toBoolean() == true)) {
          extensions.configure(SigningExtension::class.java) {
            val signingKey = project.findProperty("signingKey")?.toString()
              ?: error("Signing Key is not set, but release is turned on")
            val signingPassword = project.findProperty("signingPassword")?.toString()
              ?: error("Signing Password is not set, but release is turned on")
            it.useInMemoryPgpKeys(signingKey, signingPassword)
            it.sign(extensions.findByType(PublishingExtension::class.java)!!.publications)
          }
        }
      }
    }
  }
}
