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

package br.com.zup.nimbus.core.tree

import br.com.zup.nimbus.core.dependency.Dependency
import br.com.zup.nimbus.core.dependency.Dependent
import br.com.zup.nimbus.core.scope.Scope

/**
 * Represents a component (node) of the original json.
 *
 * The components in a json are represented as follows (Typescript):
 * interface Component {
 *   "_:component": string, // the component identifier, equivalent to "component" in this class
 *   state: State, // equivalent to a ServerDrivenState, a property of Scope
 *   id: string, // the identifier for this component
 *   properties?: Record<string, any?>, // the properties of the component
 *   children?: Component[], // the children of this component
 * }
 */
interface ServerDrivenNode: Dependency, Dependent, Scope {
  /**
   * The unique id for this component.
   */
  val id: String
  /**
   * Identifies the component to render. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Components without a namespace are core components.
   */
  val component: String
  /**
   * The property map for this component. If this component has no properties, this will be null or an empty map.
   */
  val properties: Map<String, Any?>?
  /**
   * The children of this node. If this is a leaf-node, children will be null or an empty map.
   */
  val children: List<ServerDrivenNode>?
}

/**
 * Traverses the tree using a depth-first-search algorithm. As soon as it finds a node with the specified id, it returns
 * the node.
 *
 * If a node with the specified id is not found, null is returned.
 */
fun ServerDrivenNode.findNodeById(id: String): ServerDrivenNode? {
  if (this.id == id) return this
  this.children?.forEach {
    val found = it.findNodeById(id)
    if (found != null) return found
  }
  return null
}
