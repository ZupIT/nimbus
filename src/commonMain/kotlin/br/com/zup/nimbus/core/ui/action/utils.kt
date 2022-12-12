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

import br.com.zup.nimbus.core.ActionEvent
import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.deserialization.SerializationError
import br.com.zup.nimbus.core.ui.action.error.ActionExecutionError

/**
 * Utility for attempting a json serialization from within an action handler.
 */
internal fun attemptJsonSerialization(value: AnyServerDrivenData, event: ActionEvent) =
  if (value.isNull()) null
  else try {
    value.toJson()
  } catch (e: SerializationError) {
    throw ActionExecutionError(event, e)
  }
