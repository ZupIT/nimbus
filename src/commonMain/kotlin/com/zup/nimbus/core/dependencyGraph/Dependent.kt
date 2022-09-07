package com.zup.nimbus.core.dependencyGraph

/**
 * Makes this a node in the dependency graph so it can depend on another node.
 *
 * When a dependency changes and it's time to update this, the method `update()`
 * will be called.
 */
interface Dependent {
  /**
   * Updates this node according to its dependencies.
   */
  fun update()
}
