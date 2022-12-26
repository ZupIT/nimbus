/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    propertyContainer?.update()
    metadataContainer?.update()
    properties = propertyContainer?.read()
    metadata = metadataContainer?.read()
  }

  override fun initialize(scope: Scope) {
    if (scope !is ServerDrivenEvent) throw IllegalArgumentException("Actions must be initialized with events!")
    if (hasInitialized) throw DoubleInitializationError()
    propertyContainer?.initialize(scope)
    metadataContainer?.initialize(scope)
    initHandler?.let {
      update()
      it(ActionInitializedEvent(this, scope))
    }
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
