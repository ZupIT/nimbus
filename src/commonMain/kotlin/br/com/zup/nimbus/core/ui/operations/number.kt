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

import br.com.zup.nimbus.core.ui.UILibrary
import br.com.zup.nimbus.core.utils.compareTo
import br.com.zup.nimbus.core.utils.div
import br.com.zup.nimbus.core.utils.minus
import br.com.zup.nimbus.core.utils.plus
import br.com.zup.nimbus.core.utils.times
import br.com.zup.nimbus.core.utils.toNumberOrNull

private fun toNumberList(values: List<Any?>) = values.map { toNumberOrNull(it) }

// examples: left == right; left > right; left <= right
private fun toLeftAndRight(values: List<Any?>): Pair<Number?, Number?> {
  val numberList = toNumberList(values)
  return numberList.firstOrNull() to numberList.getOrNull(1)
}

@Suppress("ComplexMethod")
internal fun registerNumberOperations(library: UILibrary) {
  library
    .addOperation("sum"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result + item }
    }
    .addOperation("subtract"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result - item }
    }
    .addOperation("multiply"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result * item }
    }
    .addOperation("divide"){
      toNumberList(it).reduce { result, item -> if (result == null || item == null) null else result / item }
    }
    .addOperation("gt"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left > right
    }
    .addOperation("gte"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left >= right
    }
    .addOperation("lt"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left < right
    }
    .addOperation("lte"){
      val (left, right) = toLeftAndRight(it)
      if (left == null || right == null) false
      else left <= right
    }
}
