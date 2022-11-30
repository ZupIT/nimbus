package br.com.zup.nimbus.core.dependency

/**
 * Makes this a node in the dependency graph so it can be a dependency of another node.
 *
 * When this changes and it's time for its dependents to update, the `update()` method of each
 * dependent will be called.
 */
interface Dependency {
  /**
   * The list of nodes that depend on this in the dependency graph.
   */
  val dependents: MutableSet<Dependent>
  /**
   * Whether or not this dependency changed since the last time its dependents were updated.
   * This must be set to false by whatever updates the dependents.
   */
  var hasChanged: Boolean
  /**
   * Makes `dependent` depend on this.
   */
  fun addDependent(dependent: Dependent)
  /**
   * Removes a dependent.
   */
  fun removeDependent(dependent: Dependent)
}
