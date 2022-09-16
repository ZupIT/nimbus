package com.zup.nimbus.core.expression

/**
 * The compiled version of an expression string, i.e., the Abstract Syntax Tree (AST).
 */
interface Expression {
  fun getValue(): Any?
}
