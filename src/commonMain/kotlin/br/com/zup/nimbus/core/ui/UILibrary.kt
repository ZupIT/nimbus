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

package br.com.zup.nimbus.core.ui

import br.com.zup.nimbus.core.ActionHandler
import br.com.zup.nimbus.core.ActionInitializationHandler
import br.com.zup.nimbus.core.OperationHandler

/**
 * Represents the UI extensions that can be made by a third-party application.
 *
 * It's considered to be a UI extension every:
 * - Action execution handler
 * - Action initializer
 * - Action observer
 * - Operation
 *
 * This class must be extended in the UI layer to include components.
 */
open class UILibrary(
  /**
   * The namespace for this library. This string prefixes every action in the library with "${namespace}:". If namespace
   * is an empty string, no prefix is added and this is considered to be an extension of the core UI library.
   *
   * Attention: this has no effect over operation names.
   */
  val namespace: String = "",
) {
  private val actions = mutableMapOf<String, ActionHandler>()
  private val actionInitializers = mutableMapOf<String, ActionInitializationHandler>()
  private val actionObservers = mutableListOf<ActionHandler>()
  private val operations = mutableMapOf<String, OperationHandler>()

  open fun addAction(name: String, handler: ActionHandler): UILibrary {
    actions[name] = handler
    return this
  }

  open fun addActionInitializer(name: String, handler: ActionInitializationHandler): UILibrary {
    actionInitializers[name] = handler
    return this
  }

  open fun addActionObserver(observer: ActionHandler): UILibrary {
    actionObservers.add(observer)
    return this
  }

  open fun addOperation(name: String, handler: OperationHandler): UILibrary {
    operations[name] = handler
    return this
  }

  fun getAction(name: String): ActionHandler? {
    return actions[name]
  }

  fun getActionInitializer(name: String): ActionInitializationHandler? {
    return actionInitializers[name]
  }

  fun getActionObservers(): List<ActionHandler> {
    return actionObservers
  }

  fun getOperation(name: String): OperationHandler? {
    return operations[name]
  }

  open fun merge(other: UILibrary): UILibrary {
    actions.putAll(other.actions)
    actionInitializers.putAll(other.actionInitializers)
    actionObservers.addAll(other.actionObservers)
    operations.putAll(other.operations)
    return this
  }
}
