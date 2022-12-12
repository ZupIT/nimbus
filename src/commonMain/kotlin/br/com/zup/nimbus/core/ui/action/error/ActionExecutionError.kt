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

package br.com.zup.nimbus.core.ui.action.error

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.scope.getPathToScope

class ActionExecutionError private constructor (
  event: ActionEvent,
  override val cause: Throwable?,
  message: String,
): Error() {
  constructor(event: ActionEvent, cause: Throwable): this(
    event,
    cause,
    cause.message ?: cause::class.qualifiedName ?: "unknown",
  )

  constructor(event: ActionEvent, message: String): this(event, null, message)

  override val message: String = "Error while running the action \"${event.action.name}\".\nAt: " +
    "${event.scope.getPathToScope()}\nCaused by: $message"
}
