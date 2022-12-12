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

package br.com.zup.nimbus.core.deserialization

class SerializationError(propertyName: String, value: Any?): Error() {
  override val message = "Can't serialize \"$propertyName\". Only the types String, Short, Int, Long, Float, Double, " +
    "Boolean, Map and List are serializable.\nThe value of \"$propertyName\" is ${valueString(value)}."

  private fun valueString(value: Any?) = value?.let {
    "of type ${value::class.qualifiedName ?: value::class.simpleName ?: value::class}, its string representation " +
      "is $value"
  } ?: "null"
}
