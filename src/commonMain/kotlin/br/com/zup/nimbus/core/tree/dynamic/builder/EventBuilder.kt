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

package br.com.zup.nimbus.core.tree.dynamic.builder

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.tree.dynamic.DynamicEvent
import br.com.zup.nimbus.core.utils.UnexpectedDataTypeError

/**
 * Builds DynamicEvents from JSON sources.
 */
class EventBuilder(nimbus: Nimbus) {
  private val actionBuilder = ActionBuilder(nimbus)

  /**
   * Verifies if this JSON structure represents an event. An event is a list of actions.
   */
  fun isJsonEvent(maybeEvent: Any?): Boolean {
    return maybeEvent is List<*> && maybeEvent.isNotEmpty() && actionBuilder.isJsonAction(maybeEvent.first()!!)
  }

  /**
   * Builds a DynamicEvent from its json map representation.
   *
   * To avoid errors, verify if `map` is an event (isJsonEvent) before calling this method.
   */
  fun buildFromJsonMap(name: String, jsonEvent: Any?): DynamicEvent {
    try {
      @Suppress("UNCHECKED_CAST")
      jsonEvent as List<Map<String, Any?>>
      val event = DynamicEvent(name)
      event.actions = jsonEvent.map { actionBuilder.buildFromJsonMap(it) }
      return event
    } catch (e: UnexpectedDataTypeError) {
      throw MalformedActionListError(e.message)
    }
  }
}
