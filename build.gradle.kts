plugins {
  id("com.github.jakemarsden.git-hooks") version "0.0.2"
}

gitHooks {
  setHooks(
    mapOf(
      "pre-commit" to "detekt",
      "pre-push" to "test"
    )
  )
}
