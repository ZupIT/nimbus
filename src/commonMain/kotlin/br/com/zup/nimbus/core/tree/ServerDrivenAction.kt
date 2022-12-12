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

import br.com.zup.nimbus.core.ActionHandler
import br.com.zup.nimbus.core.dependency.Dependent

/**
 * Represents an action of the original json, i.e. an instruction to execute a function.
 *
 * The actions in a json are represented as follows (Typescript):
 * interface Action {
 *   "_:action": string, // the action identifier, equivalent to "name" in this class
 *   properties?: Record<string, any?>, // the properties of the action
 *   metadata?: Record<string, any?>, // the metadata of the action
 * }
 */
interface ServerDrivenAction: Dependent {
  /**
   * Identifies the action to execute. This follows the pattern "namespace:name", where "namespace:" is optional.
   * Actions without a namespace are core actions.
   */
  val name: String
  /**
   * The function to run when the action is triggered (execution).
   */
  val handler: ActionHandler
  /**
   * The property map for this action. If this component has no properties, this will be null or an empty map.
   */
  val properties: Map<String, Any?>?
  /**
   * Metadata for this action. The metadata should not make a difference when executing the action, but can contain
   * useful data for action observers. Metadata properties are not specific to an action, they usually appear on
   * any action no matter its name. Example: for creating an analytics service, one might add the metadata "analytics"
   * to decide whether or not to generate a record for the action. Any kind of action would be able to receive the
   * metadata "analytics".
   */
  val metadata: Map<String, Any?>?
}
