package br.com.zup.nimbus.core.tree.dynamic

import br.com.zup.nimbus.core.ActionHandler
import br.com.zup.nimbus.core.ActionInitializationHandler
import br.com.zup.nimbus.core.ActionInitializedEvent
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.tree.ServerDrivenAction
import br.com.zup.nimbus.core.tree.ServerDrivenEvent
import br.com.zup.nimbus.core.tree.dynamic.container.PropertyContainer

/**
 * DynamicActions are a type of ServerDrivenAction that can change its properties during its lifecycle. These changes
 * are made according to expressions and states in the current tree.
 */
class DynamicAction(
  override val name: String,
  override val handler: ActionHandler,
  /**
   * The function to run once the action is initialized.
   */
  private val initHandler: ActionInitializationHandler?,
) : ServerDrivenAction, LazilyScoped<DynamicAction> {
  override var properties: Map<String, Any?>? = null
  override var metadata: Map<String, Any?>? = null
  /**
   * A container that knows how to update the dynamic properties of this action.
   */
  internal var propertyContainer: PropertyContainer? = null
  /**
   * A container that knows how to update the dynamic metadata of this action.
   */
  internal var metadataContainer: PropertyContainer? = null
  private var hasInitialized = false

  override fun update() {
    properties = propertyContainer?.read()
    metadata = metadataContainer?.read()
  }

  override fun initialize(scope: Scope) {
    if (scope !is ServerDrivenEvent) throw IllegalArgumentException("Actions must be initialized with events!")
    if (hasInitialized) throw DoubleInitializationError()
    propertyContainer?.initialize(scope)
    metadataContainer?.initialize(scope)
    propertyContainer?.addDependent(this)
    metadataContainer?.addDependent(this)
    update()
    initHandler?.let { it(ActionInitializedEvent(this, scope)) }
    hasInitialized = true
  }

  override fun clone(): DynamicAction {
    if (hasInitialized) throw CloneAfterInitializationError()
    val cloned = DynamicAction(name, handler, initHandler)
    cloned.metadataContainer = metadataContainer?.clone()
    cloned.propertyContainer = propertyContainer?.clone()
    return cloned
  }
}
