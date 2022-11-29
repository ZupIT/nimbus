package br.com.zup.nimbus.core.scope

import br.com.zup.nimbus.core.ServerDrivenState

/**
 * A simple scope to group up server driven nodes with common states without having to create a new node. Used mostly by
 * the ForEach component when making the states "item" and "index" available to its children.
 */
class StateOnlyScope(override var parent: Scope?, override val states: List<ServerDrivenState>?): Scope {
  override fun get(key: String) = parent?.get(key)

  override fun set(key: String, value: Any) {
    parent?.set(key, value)
  }

  override fun unset(key: String) {
    parent?.unset(key)
  }
}
