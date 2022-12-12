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

import br.com.zup.nimbus.core.log.LogLevel
import br.com.zup.nimbus.core.ActionTriggeredEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.action.error.ActionDeserializationError

internal fun log(event: ActionTriggeredEvent) {
  val logger = event.scope.nimbus.logger
  val properties = AnyServerDrivenData(event.action.properties)
  val message = properties.get("message").asString()
  val level = properties.get("level").asEnumOrNull(LogLevel.values()) ?: LogLevel.Info
  if (properties.hasError()) throw ActionDeserializationError(event, properties)
  logger.log(message, level)
}
