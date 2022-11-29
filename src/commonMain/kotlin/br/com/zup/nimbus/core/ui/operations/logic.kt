package br.com.zup.nimbus.core.ui.operations

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
      !(it[0] as Boolean)
    }
    .addOperation("condition") {
      val premise = it[0] as Boolean
      val trueValue = it[1]
      val falseValue = it[2]
      ((premise) then trueValue) ?: falseValue
    }
}
