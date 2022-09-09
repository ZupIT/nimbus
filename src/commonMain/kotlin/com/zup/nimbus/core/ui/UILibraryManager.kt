package com.zup.nimbus.core.ui

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializationHandler
import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.regex.toFastRegex

private val identifierRegex = "(?:(\\w+):)?(\\w+)".toFastRegex()

class UILibraryManager(libraries: List<UILibrary>? = null) {
  private val libraries = mutableMapOf<String, UILibrary>()

  init {
    this.libraries[""] = coreUILibrary
    libraries?.forEach { addLibrary(it) }
  }

  private fun <T>get(identifier: String, getter: (UILibrary, String) -> T): T? {
    val matched = identifierRegex.findWithGroups(identifier) ?: return null
    val (namespace, name) = matched.destructured
    val library = libraries[namespace]
    return library?.let { getter(it, name) }
  }

  fun getAction(identifier: String): ActionHandler? {
    return get(identifier) { lib, name ->
      lib.getAction(name)
    }
  }

  fun getActionInitializer(identifier: String): ActionInitializationHandler? {
    return get(identifier) { lib, name ->
      lib.getActionInitializer(name)
    }
  }

  fun getActionObservers(): List<ActionHandler> {
    return libraries.values.map { it.getActionObservers() }.flatten()
  }

  fun getOperation(name: String): OperationHandler? {
    libraries.forEach {
      val operation = it.value.getOperation(name)
      if (operation != null) return operation
    }
    return null
  }

  fun addLibrary(lib: UILibrary) {
    val current = libraries[lib.namespace]
    if (current == null) libraries[lib.namespace] = lib
    else current.merge(lib)
  }
}
