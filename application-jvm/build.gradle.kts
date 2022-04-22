plugins {
  kotlin("jvm") version "1.6.21"
  id("com.gradle.plugin-publish") version "0.21.0"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
  implementation("com.adarshr:gradle-test-logger-plugin:3.2.0")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.20.0")
}

gradlePlugin {
  plugins {
    create("Sourdough Application JVM") {
      id = "io.bkbn.sourdough.application.jvm"
      displayName = "JVM Application Configuration Plugin for Sourdough Projects"
      description = "Highly-opinionated, batteries-included JVM application gradle configuration plugin for multi-module projects"
      implementationClass = "io.bkbn.sourdough.gradle.application.jvm.ApplicationJvmPlugin"
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
