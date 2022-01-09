plugins {
  id("kotlin-common-conventions")
  `kotlin-dsl`
  `java-gradle-plugin`
  `maven-publish`
  id("com.gradle.plugin-publish") version "0.19.0"
}

dependencies {
  api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
  api("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
  api("org.jetbrains.dokka:versioning-plugin:1.6.10")
  api("com.adarshr:gradle-test-logger-plugin:3.1.0")
  api("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0")
  api("org.jetbrains.kotlinx:kover:0.4.4")
  api("io.github.gradle-nexus:publish-plugin:1.1.0")
}

gradlePlugin {
  plugins {
    create("Root") {
      id = "io.bkbn.sourdough.root"
      displayName = "Root Plugin for Sourdough"
      description = "Highly-opinionated, batteries-included root gradle configuration plugin"
      implementationClass = "io.bkbn.sourdough.gradle.core.RootPlugin"
    }
    create("Library") {
      id = "io.bkbn.sourdough.library"
      displayName = "Library plugin for Sourdough"
      description = "Highly-opinionated, batteries-included library configuration plugin"
      implementationClass = "io.bkbn.sourdough.gradle.core.LibraryPlugin"
    }
    create("Application") {
      id = "io.bkbn.sourdough.application"
      displayName = "Application Plugin"
      description = "Highly-opinionated, batteries-included application configuration plugin"
      implementationClass = "io.bkbn.sourdough.gradle.core.ApplicationPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/bkbnio"
  vcsUrl = "https://github.com/bkbnio/sourdough-gradle"
  tags = listOf("configuration", "kotlin")
}

publishing {
  repositories {
    maven {
      name = "GithubPackages"
      url = uri("https://maven.pkg.github.com/bkbnio/sourdough-gradle")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
