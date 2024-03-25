plugins {
  id("io.gitlab.arturbosch.detekt") version "1.23.6"
}

allprojects {
  group = "io.bkbn"
  version = run {
    val baseVersion =
      project.findProperty("project.version") ?: error("project.version needs to be set in gradle.properties")
    when ((project.findProperty("release") as? String)?.toBoolean()) {
      true -> baseVersion
      else -> "$baseVersion-SNAPSHOT"
    }
  }
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
  }
}

detekt {
  config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
  buildUponDefaultConfig = true
}

