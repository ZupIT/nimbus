package com.zup.nimbus.core.dependency

/**
 * Makes this a node in the dependency graph so it can be a dependency of another node.
 *
 * When this changes and it's time for its dependents to update, the `update()` method of each
 * dependent will be called.
 */
abstract class Dependency {
  /**
   * The list of nodes that depend on this in the dependency graph.
   */
  val dependents = HashSet<Dependent>()
  /**
   * Whether or not this dependency changed since the last time its dependents were updated.
   * This must be set to false by whatever updates the dependents.
   */
  var hasChanged = false

  /**
   * Makes `dependent` depend on this.
   */
  fun addDependent(dependent: Dependent) {
    dependents.add(dependent)
  }

  /**
   * Removes a dependent.
   */
  fun removeDependent(dependent: Dependent) {
    dependents.remove(dependent)
  }
}
