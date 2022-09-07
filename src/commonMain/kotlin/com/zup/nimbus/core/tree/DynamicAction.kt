package com.zup.nimbus.core.tree

import com.zup.nimbus.core.ActionHandler
import com.zup.nimbus.core.tree.container.PropertyContainer
import com.zup.nimbus.core.tree.stateful.ServerDrivenEvent

class DynamicAction(
  override val name: String,
  override val handler: ActionHandler,
  override var event: ServerDrivenEvent,
) : ServerDrivenAction {
  override var properties: Map<String, Any?>? = null
  override var metadata: Map<String, Any?>? = null
  private var propertyContainer: PropertyContainer? = null
  private var metadataContainer: PropertyContainer? = null

  internal fun setPropertyContainer(propertyContainer: PropertyContainer) {
    this.propertyContainer = propertyContainer
    propertyContainer.addDependent(this)
    update()
  }

  internal fun setMetadataContainer(metadataContainer: PropertyContainer) {
    this.metadataContainer = metadataContainer
    metadataContainer.addDependent(this)
    update()
  }

  override fun update() {
    properties = propertyContainer?.read()
    metadata = metadataContainer?.read()
  }
}
