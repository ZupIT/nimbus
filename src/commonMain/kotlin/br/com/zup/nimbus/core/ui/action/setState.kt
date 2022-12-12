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

import br.com.zup.nimbus.core.regex.toFastRegex
import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.scope.closestState
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError
import br.com.zup.nimbus.core.ui.action.error.ActionExecutionError

private val statePathRegex = """^(\w+)((?:\.\w+)*)${'$'}""".toFastRegex()

internal fun setState(event: ActionTriggeredEvent) {
  val properties = AnyServerDrivenData(event.action.properties)
  val sourceEvent = event.scope
  val path = properties.get("path").asString()
  val value = properties.get("value").asAnyOrNull()
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  val matchResult = statePathRegex.findWithGroups(path)
    ?: throw ActionExecutionError(event, IllegalArgumentException("""The path "$path" is not a valid state path."""))
  val (stateId, statePath) = matchResult.destructured
  val state = sourceEvent.closestState(stateId)
    ?: throw ActionExecutionError(event, IllegalArgumentException("""Could not find state "$stateId""""))
  state.set(value, statePath, false)
  event.dependencies.add(state)
}
