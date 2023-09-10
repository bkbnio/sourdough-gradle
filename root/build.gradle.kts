plugins {
  kotlin("jvm") version "1.9.10"
  id("com.gradle.plugin-publish") version "1.2.1"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.kotlinx.kover:org.jetbrains.kotlinx.kover.gradle.plugin:0.7.3")
  implementation("io.github.gradle-nexus:publish-plugin:1.3.0")
}

gradlePlugin {
  plugins {
    create("Sourdough Root") {
      id = "io.bkbn.sourdough.root"
      displayName = "Root Plugin for Sourdough Projects"
      description = "Highly-opinionated, batteries-included root gradle configuration plugin for multi-module projects"
      implementationClass = "io.bkbn.sourdough.gradle.root.RootPlugin"
    }
  }
}

gradlePlugin {
  website = "https://github.com/bkbnio"
  vcsUrl = "https://github.com/bkbnio/sourdough-gradle"
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
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
