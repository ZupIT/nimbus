package com.zup.nimbus.core.expression

class Literal(private val value: Any?): Expression {
  override fun getValue(): Any? {
    return value
  }
}
