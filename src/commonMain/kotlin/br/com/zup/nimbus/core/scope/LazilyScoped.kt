package br.com.zup.nimbus.core.scope

/**
 * An entity that needs a scope, but the scope can't be known upon construction, i.e. it must be lazily initialized.
 * LazilyScoped entities must also be able to copy themselves as long as a scope hasn't been assigned yet.
 */
interface LazilyScoped<T> {
  /**
   * Initializes this entity with the given scope.
   *
   * @throws DoubleInitializationError if called more than once.
   */
  fun initialize(scope: Scope)
  /**
   * Deep copies this entity. This can only be called before initialize.
   *
   * @throws CloneAfterInitializationError if called after the initialization.
   */
  fun clone(): T
}

class DoubleInitializationError: IllegalStateException(
  "Can't initialize this LazilyScoped instance because it has already been initialized!"
)

class CloneAfterInitializationError: IllegalStateException(
  "Can't clone this LazilyScoped instance because it has already been initialized!!"
)
