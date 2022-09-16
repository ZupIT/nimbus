package com.zup.nimbus.core.tree

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.ActionInitializationHandler
import com.zup.nimbus.core.ActionInitializedEvent
import com.zup.nimbus.core.scope.CloneAfterInitializationError
import com.zup.nimbus.core.scope.DoubleInitializationError
import com.zup.nimbus.core.scope.Scope
import com.zup.nimbus.core.tree.container.PropertyContainer

class DynamicAction(
  override val name: String,
  override val handler: ActionHandler,
  private val initHandler: ActionInitializationHandler?,
) : ServerDrivenAction {
  override var properties: Map<String, Any?>? = null
  override var metadata: Map<String, Any?>? = null
  internal var propertyContainer: PropertyContainer? = null
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
    initHandler?.let { it(ActionInitializedEvent(this, scope)) }
    hasInitialized = true
    update()
  }

  override fun clone(): ServerDrivenAction {
    if (hasInitialized) throw CloneAfterInitializationError()
    val cloned = DynamicAction(name, handler, initHandler)
    cloned.metadataContainer = metadataContainer?.clone()
    cloned.propertyContainer = propertyContainer?.clone()
    return cloned
  }
}
