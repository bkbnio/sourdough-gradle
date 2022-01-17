plugins {
  kotlin("jvm") version "1.6.10"
  id("com.gradle.plugin-publish") version "0.19.0"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
  implementation("com.adarshr:gradle-test-logger-plugin:3.1.0")
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
  implementation("org.jetbrains.dokka:versioning-plugin:1.6.10")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0")
}

gradlePlugin {
  plugins {
    create("Sourdough Library JVM") {
      id = "io.bkbn.sourdough.library.jvm"
      displayName = "JVM Library Configuration Plugin for Sourdough Projects"
      description = "Highly-opinionated, batteries-included jvm library gradle configuration plugin for multi-module projects"
      implementationClass = "io.bkbn.sourdough.gradle.library.jvm.LibraryJvmPlugin"
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