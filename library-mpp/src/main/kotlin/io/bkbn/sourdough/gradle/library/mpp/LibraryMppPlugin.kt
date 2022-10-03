package io.bkbn.sourdough.gradle.library.mpp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.util.Locale

class LibraryMppPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.extensions.create("sourdoughLibrary", LibraryMppExtension::class.java)
    target.configurePublishing()
    target.configureSigning()
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
