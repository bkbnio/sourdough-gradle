package io.bkbn.sourdough.gradle.library.mpp

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import java.util.Locale

class LibraryMppPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val ext = target.extensions.create("sourdough", LibraryMppExtension::class.java)
    target.configureDetekt()
    target.configureTesting()
    target.configureDokka()
    target.configureMultiplatform(ext)
    target.configureNodeJS(ext)
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

  private fun Project.configureMultiplatform(ext: LibraryMppExtension) {
    extensions.configure(KotlinMultiplatformExtension::class.java) {
      it.jvm {
        compilations.all {
          it.kotlinOptions.jvmTarget = ext.jvmTarget.get()
        }
        withJava()
        // todo defer to test suites?
        testRuns.getByName("test").executionTask.configure {
          it.useJUnitPlatform()
        }
      }
      it.js(KotlinJsCompilerType.IR) {
        browser {
          commonWebpackConfig {
            cssSupport.enabled = true
          }
          testTask {
            useKarma {
              useChromeCanaryHeadless()
            }
          }
        }
      }
      val hostOs = System.getProperty("os.name")
      val cpuArch = System.getProperty("os.arch")
      val isMingwX64 = hostOs.startsWith("Windows")

      when {
        hostOs == "Mac OS X" -> when (cpuArch) {
          "aarch64" -> it.macosArm64("native")
          else -> it.macosX64("native")
        }
        hostOs == "Linux" -> it.linuxX64("native")
        isMingwX64 -> it.mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
      }
    }
  }

  private fun Project.configureNodeJS(ext: LibraryMppExtension) {
    rootProject.plugins.withType(NodeJsRootPlugin::class.java) {
      rootProject.extensions.configure(NodeJsRootExtension::class.java) {
        it.nodeVersion = ext.nodeJsVersion.get()
      }
    }
  }

  // TODO This is borked
  private fun Project.configurePublishing() {
    afterEvaluate {
      extensions.findByType(LibraryMppExtension::class.java)?.let { ext ->
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
//                mpub.artifact(tasks.findByName("sourcesJar"))
//                mpub.artifact(tasks.findByName("javadocJar"))
                mpub.groupId = project.group.toString()
                mpub.artifactId = project.name.lowercase(Locale.getDefault())
                mpub.version = project.version.toString()

                mpub.pom { mpom ->
                  mpom.name.set(ext.libraryName)
                  mpom.description.set(ext.libraryDescription)
                  mpom.url.set("https://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}")
                  mpom.licenses { mpls ->
                    mpls.license { mpl ->
                      mpl.name.set(ext.licenseName)
                      mpl.url.set(ext.licenseUrl)
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
