plugins {
  kotlin("jvm") version "1.9.23"
  id("com.gradle.plugin-publish") version "1.2.1"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
  implementation("com.adarshr:gradle-test-logger-plugin:4.0.0")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.5")
  implementation("org.jetbrains.kotlinx.kover:org.jetbrains.kotlinx.kover.gradle.plugin:0.7.6")
}

gradlePlugin {
  website = "https://github.com/bkbnio"
  vcsUrl = "https://github.com/bkbnio/sourdough-gradle"
  plugins {
    create("Sourdough Library JVM") {
      id = "io.bkbn.sourdough.library.jvm"
      displayName = "JVM Library Configuration Plugin for Sourdough Projects"
      description = "Highly-opinionated, batteries-included jvm library gradle configuration plugin for multi-module projects"
      implementationClass = "io.bkbn.sourdough.gradle.library.jvm.LibraryJvmPlugin"
      tags = listOf("configuration", "kotlin")
    }
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
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
