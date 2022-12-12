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

open class MalformedJsonError(override val message: String): Error("$message Please check your json string.")

class MalformedComponentError(
  id: String?,
  jsonPath: String,
  cause: String? = null,
): MalformedJsonError("") {
  private val idText = if (id == null) "" else """ id "$id" and"""
  private val causeText = if (cause == null) "" else "\nCause: $cause"
  override val message = "Error while trying to deserialize component with$idText JSONPath \"$jsonPath\".$causeText"
}

class MalformedActionListError(cause: String): MalformedJsonError("The list of actions is mal-formed.\nCause: $cause")
