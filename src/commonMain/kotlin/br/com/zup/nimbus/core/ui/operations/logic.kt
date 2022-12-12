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

package br.com.zup.nimbus.core.ui.operations

import br.com.zup.nimbus.core.deserialization.AnyServerDrivenData
import br.com.zup.nimbus.core.ui.UILibrary
import br.com.zup.nimbus.core.utils.then

private fun toBooleanList(values: List<Any?>): List<Boolean> {
  return values.filterIsInstance<Boolean>()
}

internal fun registerLogicOperations(library: UILibrary) {
  library
    .addOperation("and") {
      !toBooleanList(it).contains(false)
    }
    .addOperation("or") {
      toBooleanList(it).contains(true)
    }
    .addOperation("not") {
      val arguments = AnyServerDrivenData(it)
      val value = arguments.at(0).asBoolean()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      !value
    }
    .addOperation("condition") {
      val arguments = AnyServerDrivenData(it)
      val premise = arguments.at(0).asBoolean()
      val trueValue = arguments.at(1).asAnyOrNull()
      val falseValue = arguments.at(2).asAnyOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (premise) trueValue else falseValue
    }
}
