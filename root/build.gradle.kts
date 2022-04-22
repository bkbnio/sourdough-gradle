plugins {
  kotlin("jvm") version "1.6.21"
  id("com.gradle.plugin-publish") version "0.21.0"
  id("java-gradle-plugin")
  id("maven-publish")
}

dependencies {
  implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.20")
  implementation("org.jetbrains.dokka:versioning-plugin:1.6.20")
  implementation("org.jetbrains.kotlinx:kover:0.5.0")
  implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
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
