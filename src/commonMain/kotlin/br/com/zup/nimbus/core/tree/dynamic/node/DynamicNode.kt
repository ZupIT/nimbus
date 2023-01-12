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

package br.com.zup.nimbus.core.tree.dynamic.node

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.scope.CloneAfterInitializationError
import br.com.zup.nimbus.core.scope.DoubleInitializationError
import br.com.zup.nimbus.core.ServerDrivenState
import br.com.zup.nimbus.core.dependency.CommonDependency
import br.com.zup.nimbus.core.dependency.Dependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.scope.CommonScope
import br.com.zup.nimbus.core.scope.LazilyScoped
import br.com.zup.nimbus.core.scope.Scope
import br.com.zup.nimbus.core.scope.closestScopeWithType
import br.com.zup.nimbus.core.tree.ServerDrivenNode
import br.com.zup.nimbus.core.tree.dynamic.container.NodeContainer
import br.com.zup.nimbus.core.tree.dynamic.container.PropertyContainer

/**
 * DynamicNodes are a type of ServerDrivenNode that can change its properties and children during its lifecycle. These
 * changes are made according to expressions and states in the current tree.
 */
open class DynamicNode(
  override val id: String,
  override val component: String,
  states: List<ServerDrivenState>?,
  /**
   * A DynamicNode is said to be polymorphic if it can turn itself into another node in respect to the final UI tree
   * (rendered by the UI layer). Polymorphic nodes can be invisible at a time, they can also be a single node or
   * multiple nodes. They can at a time be a button and at another be a text, for instance.
   *
   * Polymorphic nodes exist in the data structure, but how they appear in the tree read by the UI Layer depends on
   * their own logic. Examples of Polymorphic nodes are: If and ForEach, which calculate their children based on the
   * current value of its properties. Since a NodeContainer skips every polymorphic node by only getting its children,
   * they never appear in the list of children of a DynamicNode.
   *
   * For now, we don't intend to make polymorphic nodes an open feature and this boolean works. If this changes, we'll
   * probably need PolymorphicNode to be a subclass of DynamicNode.
   */
  val polymorphic: Boolean = false,
): CommonDependency(), Scope by CommonScope(states), LazilyScoped<DynamicNode>, ServerDrivenNode {
  override var properties: Map<String, Any?>? = null
  override var children: List<DynamicNode>? = null
  /**
   * A container that knows how to update the dynamic properties of this node.
   */
  internal var propertyContainer: PropertyContainer? = null
  /**
   * A container that knows how to update the dynamic children of this node.
   */
  internal var childrenContainer: NodeContainer? = null

  override fun update() {
    propertyContainer?.let { properties = it.read() }
    childrenContainer?.let { children = it.read() }
    hasChanged = true
  }

  /**
   * Compute the values of states that have been provided expressions as their initial values.
   */
  private fun initializeDependentStates() {
    if (states?.isEmpty() != false) return
    val expressionParser = closestScopeWithType<Nimbus>()?.expressionParser ?: return

    states?.forEach { state ->
      val value = state.get()
      if (value is String && expressionParser.containsExpression(value)) {
        val parsed = expressionParser.parseString(value, true)
        if (parsed is LazilyScoped<*>) parsed.initialize(this)
        if (parsed is Dependent) parsed.update()
        state.setSilently(parsed.getValue())
      }
    }
  }

  override fun initialize(scope: Scope) {
    if (parent != null) throw DoubleInitializationError()
    parent = scope
    initializeDependentStates()
    propertyContainer?.initialize(this)
    childrenContainer?.initialize(this)
    propertyContainer?.addDependent(this)
    childrenContainer?.addDependent(this)
    update()
    hasChanged = false
  }

  protected fun clone(idSuffix: String, builder: (String, List<ServerDrivenState>?) -> DynamicNode): DynamicNode {
    if (parent != null) throw CloneAfterInitializationError()
    val cloned = builder("$id$idSuffix", states?.map { it.clone() })
    cloned.propertyContainer = propertyContainer?.clone()
    cloned.childrenContainer = childrenContainer?.clone(idSuffix)
    return cloned
  }

  /**
   * Copies this DynamicNode altering only its id. The id is suffixed with the String passed as parameter.
   */
  open fun clone(idSuffix: String): DynamicNode = clone(idSuffix) { id, states ->
    DynamicNode(id, component, states)
  }

  override fun clone(): DynamicNode = clone("")
}
