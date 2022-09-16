package com.zup.nimbus.core.scope

import com.zup.nimbus.core.ServerDrivenState

interface Scope {
  var parent: Scope?
  val states: List<ServerDrivenState>?
  fun get(key: String): Any?
  fun set(key: String, value: Any)
  fun unset(key: String)
  fun closestState(id: String): ServerDrivenState?
}

internal inline fun <reified T: Scope> Scope.getParentScopeWithType(): T? {
  var current: Scope? = this
  while (current != null && current !is T) current = current.parent
  return current?.let { current as T }
}
