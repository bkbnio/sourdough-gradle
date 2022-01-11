package io.bkbn.sourdough.gradle.library.jvm

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Locale

class LibraryJvmPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdough", LibraryJvmExtension::class.java)
    target.configureDetekt()
    target.configureJava()
    target.configureKotlin(ext)
    target.configureTesting()
    target.configureDokka()
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

  private fun Project.configureJava() {
    plugins.withType(JavaLibraryPlugin::class.java) {
      extensions.configure(JavaPluginExtension::class.java) {
        it.withSourcesJar()
        it.withJavadocJar()
      }
    }
  }

  private fun Project.configureKotlin(ext: LibraryJvmExtension) {
    tasks.withType(KotlinCompile::class.java) {
      it.sourceCompatibility = ext.jvmTarget.get()
      it.kotlinOptions {
        jvmTarget = ext.jvmTarget.get()
        freeCompilerArgs = freeCompilerArgs + ext.compilerArgs.get()
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

  private fun Project.configureDokka() {
    plugins.withType(DokkaPlugin::class.java) {
      beforeEvaluate {
        it.buildscript.apply {
          dependencies.apply {
            this.addProvider("classpath", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
          }
        }
      }
      tasks.withType(DokkaTaskPartial::class.java) {
        dependencies.apply {
          addProvider("dokkaPlugin", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
        }
        it.pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
          setVersion(rootProject.version)
        }
        it.dokkaSourceSets.apply {
          configureEach { gdssb ->
            val moduleMd = projectDir.resolve("Module.md")
            if (moduleMd.exists()) {
              gdssb.includes.from("Module.md")
            }
          }
        }
      }
    }
  }

  private fun Project.configurePublishing() {
    afterEvaluate {
      extensions.findByType(LibraryJvmExtension::class.java)?.let { ext ->
        plugins.withType(MavenPublishPlugin::class.java) {
          extensions.configure(PublishingExtension::class.java) { pe ->
            pe.repositories { rh ->
              rh.maven { mar ->
                mar.name = "GithubPackages"
                mar.url = uri("https://maven.pkg.github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}")
                mar.credentials { pc ->
                  pc.username = ext.githubActor.get()
                  pc.password = ext.githubToken.get()
                }
              }
            }
            pe.publications { pc ->
              pc.create(project.name, MavenPublication::class.java) { mpub ->
                mpub.from(components.findByName("kotlin"))
                mpub.artifact(tasks.findByName("sourcesJar"))
                mpub.artifact(tasks.findByName("javadocJar"))
                mpub.groupId = project.group.toString()
                mpub.artifactId = project.name.lowercase(Locale.getDefault())
                mpub.version = project.version.toString()

                mpub.pom { mpom ->
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
