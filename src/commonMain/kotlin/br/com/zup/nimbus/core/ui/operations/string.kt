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
import br.com.zup.nimbus.core.regex.replace
import br.com.zup.nimbus.core.regex.matches
import br.com.zup.nimbus.core.regex.toFastRegex

private fun getSingleArgument(argumentList: List<Any?>): String {
  val arguments = AnyServerDrivenData(argumentList)
  val str = arguments.at(0).asString()
  if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
  return str
}

internal fun registerStringOperations(library: UILibrary) {
  library
    .addOperation("capitalize"){
      getSingleArgument(it).replaceFirstChar { char -> char.uppercaseChar() }
    }
    .addOperation("lowercase"){
      getSingleArgument(it).lowercase()
    }
    .addOperation("uppercase"){
      getSingleArgument(it).uppercase()
    }
    .addOperation("match"){
      val arguments = AnyServerDrivenData(it)
      val value = arguments.at(0).asString()
      val regex = arguments.at(1).asString()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      value.matches(regex.toFastRegex())
    }
    .addOperation("replace"){
      val arguments = AnyServerDrivenData(it)
      val value = arguments.at(0).asString()
      val regex = arguments.at(1).asString()
      val replace = arguments.at(2).asString()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      value.replace(regex.toFastRegex(), replace)
    }
    .addOperation("substr"){
      val arguments = AnyServerDrivenData(it)
      val value = arguments.at(0).asString()
      val start = arguments.at(1).asInt()
      val end = arguments.at(2).asIntOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (end == null) value.substring(start) else value.substring(start, end)
    }
}
