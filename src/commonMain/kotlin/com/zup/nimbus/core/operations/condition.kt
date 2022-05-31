package com.zup.nimbus.core.operations

import com.zup.nimbus.core.utils.then

fun condition(premise: Boolean, trueValue: Any, falseValue: Any): Any {
  return ((premise) then trueValue) ?: falseValue
}
