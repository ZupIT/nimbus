package com.zup.nimbus.core.scope

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.ServerDrivenState
import com.zup.nimbus.core.ServerDrivenView
import com.zup.nimbus.core.tree.ServerDrivenEvent
import com.zup.nimbus.core.tree.ServerDrivenNode

/**
 * Most things in Nimbus occurs within a scope. An event, for instance, can be triggered in the scope of button click,
 * which is in the scope of a component (node).
 *
 * The scope allows us to recover important information about the event, component, view and even access objects stored
 * in the Nimbus Scope, like the logger and the HttpClient.
 *
 * The Scope is nothing more than a tree, if we don't find what we're looking for in the current scope, we look into
 * the parent scope, until we get to the root.
 *
 * Normally, but not necessarily, a Scope tree will follow a format like this: Nimbus > View > RootNode > Node A >
 * Node B > Event A > Event B.
 *
 * A Scope is also responsible for holding states, which can be set by the operation `setState` and read by expressions.
 * Global states will be in `Nimbus`, states specific to a view in `ServerDrivenView` and local states in their
 * respective nodes.
 *
 * A Scope can also work as a storage unit through its methods get and set. This is useful for registering dependencies
 * in the UI Layer.
 */
interface Scope {
  /**
   * The parent scope. Null if this is the root node.
   *
   * Attention: considering a ServerDrivenNode, this is not necessarily the parent UI Node.
   */
  var parent: Scope?
  /**
   * The states associated to this scope.
   */
  val states: List<ServerDrivenState>?
  /**
   * Gets a value from this scope. If not found, it will look into the parent scope, recursively, until the root is
   * reached.
   */
  fun get(key: String): Any?
  /**
   * Sets a value to this scope.
   */
  fun set(key: String, value: Any)
  /**
   * Removes a value from this scope.
   */
  fun unset(key: String)
}

/**
 * Returns the closest Scope with the specified type.
 */
inline fun <reified T: Scope> Scope.closestScopeWithType(): T? {
  var current: Scope? = this
  while (current != null && current !is T) current = current.parent
  return current?.let { current as T }
}

/**
 * Returns the closest state with the specified id.
 */
fun Scope.closestState(id: String): ServerDrivenState? {
  return states?.find { it.id == id } ?: parent?.closestState(id)
}

/**
 * Gets a string representing the full path from the root scope to this scope.
 */
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
