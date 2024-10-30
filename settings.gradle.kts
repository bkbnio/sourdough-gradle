rootProject.name = "sourdough-gradle"

include("root")
include("application-jvm")
include("library-jvm")

// Set Project Gradle Names
run {
  rootProject.children.forEach { it.name = "${rootProject.name}-${it.name}" }
}
