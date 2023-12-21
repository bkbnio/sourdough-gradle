plugins {
  kotlin("jvm") version "1.9.22"
  id("com.gradle.plugin-publish") version "1.2.1"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
  implementation("com.adarshr:gradle-test-logger-plugin:3.2.0")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.4")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
  website = "https://github.com/bkbnio"
  vcsUrl = "https://github.com/bkbnio/sourdough-gradle"
  plugins {
    create("Multiplatform Sourdough Library") {
      id = "io.bkbn.sourdough.library.mpp"
      displayName = "Kotlin Multiplatform Library Configuration Plugin for Sourdough Projects"
      description = "Highly-opinionated, batteries-included multiplatform library gradle configuration plugin for multi-module projects"
      implementationClass = "io.bkbn.sourdough.gradle.library.mpp.LibraryMppPlugin"
      tags = listOf("configuration", "kotlin")
    }
  }
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
