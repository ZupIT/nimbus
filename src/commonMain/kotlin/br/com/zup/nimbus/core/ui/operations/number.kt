package br.com.zup.nimbus.core.ui.operations

import br.com.zup.nimbus.core.ui.UILibrary
import br.com.zup.nimbus.core.utils.compareTo
import br.com.zup.nimbus.core.utils.div
import br.com.zup.nimbus.core.utils.minus
import br.com.zup.nimbus.core.utils.plus
import br.com.zup.nimbus.core.utils.times
import br.com.zup.nimbus.core.utils.toNumberOrNull

private fun toNumberList(values: List<Any?>) = values.map { toNumberOrNull(it) }

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
      val (left, right) = toNumberList(it)
      if (left == null || right == null) false
      else left > right
    }
    .addOperation("gte"){
      val (left, right) = toNumberList(it)
      if (left == null || right == null) false
      else left >= right
    }
    .addOperation("lt"){
      val (left, right) = toNumberList(it)
      if (left == null || right == null) false
      else left < right
    }
    .addOperation("lte"){
      val (left, right) = toNumberList(it)
      if (left == null || right == null) false
      else left <= right
    }
}
