package io.bkbn.sourdough.gradle.root

import org.gradle.api.provider.Property
import java.net.URI

abstract class RootExtension {
  abstract val documentationFolder: Property<String>

  init {
    documentationFolder.convention("docs")
  }
}
