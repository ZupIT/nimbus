package com.zup.nimbus.core.scope

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.node.ServerDrivenNode

interface Scope {
  var parent: Scope?
  val states: List<ServerDrivenState>?
  fun get(key: String): Any?
  fun set(key: String, value: Any)
  fun unset(key: String)
}

internal inline fun <reified T: Scope> Scope.closestScopeWithType(): T? {
  var current: Scope? = this
  while (current != null && current !is T) current = current.parent
  return current?.let { current as T }
}

fun Scope.closestState(id: String): ServerDrivenState? {
  return states?.find { it.id == id } ?: parent?.closestState(id)
}

fun Scope.getPathToScope(): String {
  val pathToParent = this.parent?.let { "${it.getPathToScope()} > " } ?: ""
  val thisIdentifier = when (this) {
    is ServerDrivenNode -> "${this.id} (${this.component})"
    is ServerDrivenEvent -> this.name
    is ServerDrivenView -> this.description ?: "Unknown View"
    is Nimbus -> "Nimbus instance"
    else -> pathToParent
  }
  return "$pathToParent$thisIdentifier"
}
