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

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.ServerDrivenView
import br.com.zup.nimbus.core.scope.Scope

/**
 * ServerDrivenEvents represent a list of actions in the original json.
 *
 * A ServerDrivenEvent is a scope that always create a state with the same name as the key of the property who had the
 * list of actions as its value. The value of this state can be changed through the method run(Any?).
 *
 * Example: the onChange event of a TextInput will create the state "onChange" with null as its initial value. When the
 * user types in the text input created in the UI Layer, it must call run with the new value of the input, making the
 * information available for every action within this event through the state "onChange".
 *
 * Since the event always create a state, it is possible that an event overwrites another state. For instance, if
 * you have a root state called "onPress" and a button component with an event with the same name, the root state
 * "onPress" won't be accessible from within the actions of the event. Renaming the root state would solve this issue.
 */
interface ServerDrivenEvent: Scope {
  /**
   * The name of the event, i.e. the key for the property where the value was the list of actions that compose this
   * event. Examples of common event names: "onPress", "onClick", "onInit", "onSuccess", "onChange".
   */
  val name: String
  /**
   * The node that originated this event.
   */
  val node: ServerDrivenNode
  /**
   * The view that originated this event.
   */
  val view: ServerDrivenView
  /**
   * The nimbus instance that originated this event.
   */
  val nimbus: Nimbus
  /**
   * The actions contained in this event
   */
  val actions: List<ServerDrivenAction>
  /**
   * Runs the current event by triggering all actions contained in it.
   */
  fun run()
  /**
   * Sets the current value of the state created by this event and then runs it.
   */
  fun run(implicitStateValue: Any?)
}
