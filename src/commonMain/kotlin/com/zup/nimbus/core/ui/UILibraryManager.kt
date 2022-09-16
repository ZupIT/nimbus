package com.zup.nimbus.core.ui

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializationHandler
import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.regex.toFastRegex

private val identifierRegex = "(?:(\\w+):)?(\\w+)".toFastRegex()

class NamespaceName(val namespace: String, val name: String) {
  operator fun component1(): String = namespace
  operator fun component2(): String = name
}

class UILibraryManager(coreLibrary: UILibrary, customLibraries: List<UILibrary>? = null) {
  private val libraries = mutableMapOf<String, UILibrary>()

  companion object {
    fun splitIdentifier(identifier: String): NamespaceName? {
      val matched = identifierRegex.findWithGroups(identifier) ?: return null
      val (namespace, name) = matched.destructured
      return NamespaceName(namespace, name)
    }
  }

  init {
    addLibrary(coreLibrary)
    customLibraries?.forEach { addLibrary(it) }
  }

  private fun <T>get(identifier: String, getter: (UILibrary, String) -> T): T? {
    val (namespace, name) = splitIdentifier(identifier) ?: return null
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

  fun getLibrary(namespace: String): UILibrary? {
    return libraries[namespace]
  }

  fun addLibrary(lib: UILibrary) {
    val current = libraries[lib.namespace]
    if (current == null) libraries[lib.namespace] = lib
    else current.merge(lib)
  }
}
