plugins {
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt",
      "pre-push" to "test"
    )
  )
}
