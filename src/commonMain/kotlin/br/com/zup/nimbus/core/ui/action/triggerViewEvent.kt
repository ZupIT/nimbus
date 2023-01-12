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

package br.com.zup.nimbus.core.ui.action

import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError

internal fun triggerViewEvent(event: ActionTriggeredEvent) {
  val data = AnyServerDrivenData(event.action.properties)
  val nameOfEventToTrigger = data.get("event").asString()
  val valueForEventToTrigger = data.get("value").asAnyOrNull()
  if (data.hasError()) {
    throw ActionDeserializationError(event, data)
  }
  val eventToTrigger = event.scope.view.events?.find { it.name == nameOfEventToTrigger }
  if (eventToTrigger == null) {
    event.scope.nimbus.logger.error("Can't trigger view event named \"$nameOfEventToTrigger\" because the current " +
      "view has no such event.")
  } else {
    eventToTrigger.run(valueForEventToTrigger)
  }
}
