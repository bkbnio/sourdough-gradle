package io.bkbn.sourdough.gradle.core

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
    target.configureJava()
    target.configurePublishing()
    target.configureSigning()
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

  private fun Project.configurePublishing() {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
      // TODO This needs to be configurable via extension
      repositories {
        maven {
          name = "GithubPackages"
          url = uri("https://maven.pkg.github.com/bkbnio/kompendium")
          credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
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
            name.set("Kompendium") // todo configure
            description.set("A minimally invasive OpenAPI spec generator for Ktor")
            url.set("https://github.com/bkbnio/kompendium")
            licenses {
              license {
                name.set("MIT License")
                url.set("https://mit-license.org/")
              }
            }
            developers {
              developer {
                id.set("bkbnio")
                name.set("Ryan Brink")
                email.set("admin@bkbn.io")
              }
            }
            scm {
              connection.set("scm:git:git://github.com/bkbnio/Kompendium.git")
              developerConnection.set("scm:git:ssh://github.com/bkbnio/Kompendium.git")
              url.set("https://github.com/bkbnio/Kompendium.git")
            }
          }
        }
      }
    }
  }

  private fun Project.configureSigning() {
    apply(plugin = "signing")
    // todo does this need to be after evaluation?
    if ((project.findProperty("release") as? String)?.toBoolean() == true) {
      configure<SigningExtension> {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(project.name)
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
      val version = version.toString()
      val dhp = tasks.getByName("dokkaHtmlPartial") as DokkaTaskPartial
      dhp.apply {
        dependencies {
          addProvider("dokkaPlugin", provider { "org.jetbrains.dokka:versioning-plugin:1.6.0" })
        }
        pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
          setVersion(version)
        }
      }
    }
  }
}
