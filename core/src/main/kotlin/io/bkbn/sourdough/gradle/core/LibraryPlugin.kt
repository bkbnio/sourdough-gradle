package io.bkbn.sourdough.gradle.core

import io.bkbn.sourdough.gradle.core.extension.SourdoughLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.buildscript
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

class LibraryPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val extension = target.extensions.create<SourdoughLibraryExtension>("sourdough")
    target.configureJava()
    target.configurePublishing(extension)
//    target.configureSigning()
    target.configureDokka()
  }

  private fun Project.configureJava() {
    apply(plugin = "java")
    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
      withSourcesJar()
      withJavadocJar()
    }
  }

  private fun Project.configurePublishing(ext: SourdoughLibraryExtension) {
    apply(plugin = "maven-publish")
    afterEvaluate {
      configure<PublishingExtension> {
        repositories {
          maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}")
            credentials {
              username = ext.githubActor.get()
              password = ext.githubToken.get()
            }
          }
        }
        publications {
          create<MavenPublication>(project.name) {
            from(components["kotlin"])
            artifact(tasks.findByName("sourcesJar"))
            artifact(tasks.findByName("javadocJar"))
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            pom {
              name.set(ext.libraryName)
              description.set(ext.libraryDescription)
              // todo can this be improved?
              url.set("https://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}")
              licenses {
                license {
                  name.set(ext.licenseName)
                  url.set(ext.licenseUrl)
                }
              }
              developers {
                developer {
                  id.set(ext.developerId)
                  name.set(ext.developerName)
                  email.set(ext.developerEmail)
                }
              }
              // todo can this be improved
              scm {
                connection.set("scm:git:git://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
                developerConnection.set("scm:git:ssh://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
                url.set("https://github.com/${ext.githubOrg.get()}/${ext.githubRepo.get()}.git")
              }
            }
          }
        }
      }
    }
  }

  @Suppress("UnusedPrivateMember")
  private fun Project.configureSigning() {
    apply(plugin = "signing")
    afterEvaluate {
      if ((project.findProperty("release") as? String)?.toBoolean() == true) {
        configure<SigningExtension> {
          val signingKey: String? by project
          val signingPassword: String? by project
          useInMemoryPgpKeys(signingKey, signingPassword)
          sign(extensions.findByType(PublishingExtension::class.java)!!.publications)
        }
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
      tasks.withType(DokkaTaskPartial::class.java) {
        val version = version.toString()
        dependencies {
          addProvider("dokkaPlugin", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
        }
        pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
          setVersion(version)
        }
        dokkaSourceSets.apply {
          configureEach {
            val moduleMd = projectDir.resolve("Module.md")
            if (moduleMd.exists()) {
              includes.from("Module.md")
            }
          }
        }
      }
    }
  }
}
