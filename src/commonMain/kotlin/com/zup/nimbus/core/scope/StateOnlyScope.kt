package com.zup.nimbus.core.scope

import com.zup.nimbus.core.ServerDrivenState

class StateOnlyScope(override var parent: Scope?, override val states: List<ServerDrivenState>?): Scope {
  override fun get(key: String) = parent?.get(key)

  override fun set(key: String, value: Any) {
    parent?.set(key, value)
  }

  override fun unset(key: String) {
    parent?.unset(key)
  }
}
