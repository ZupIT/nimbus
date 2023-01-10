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

internal fun registerArrayOperations(library: UILibrary) {
  library
    .addOperation("array") { it }
    .addOperation("insert") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val item = arguments.at(1).asAnyOrNull()
      val index = arguments.at(2).asIntOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (index == null) list.add(item) else list.add(index, item)
      list
    }
    .addOperation("remove") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val item = arguments.at(1).asAnyOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      list.remove(item)
      list
    }
    .addOperation("removeIndex") {
      val arguments = AnyServerDrivenData(it)
      val list = if (arguments.at(0).isList()) (arguments.at(0).value as List<*>).toMutableList()
      else ArrayList()
      val index = arguments.at(1).asIntOrNull()
      if (arguments.hasError()) throw IllegalArgumentException(arguments.errorsAsString())
      if (index == null) list.removeLast() else list.removeAt(index)
      list
    }
}
