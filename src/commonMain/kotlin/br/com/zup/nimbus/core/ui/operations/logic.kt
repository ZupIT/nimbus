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
