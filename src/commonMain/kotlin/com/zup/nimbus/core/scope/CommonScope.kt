package com.zup.nimbus.core.scope

import com.zup.nimbus.core.ServerDrivenState

open class CommonScope(
  override val states: List<ServerDrivenState>?,
  override var parent: Scope? = null,
): Scope {
  private val storage = mutableMapOf<String, Any>()

  override fun get(key: String): Any? {
    return storage[key] ?: parent?.get(key)
  }

  override fun set(key: String, value: Any) {
    storage[key] = value
  }

  override fun unset(key: String) {
    storage.remove(key)
  }
}
