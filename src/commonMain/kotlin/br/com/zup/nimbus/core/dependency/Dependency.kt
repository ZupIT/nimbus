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
