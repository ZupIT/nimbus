package com.zup.nimbus.core.expression

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.tree.stateful.Stateful

//Do not remove the Redundant character escape '\}' in RegExp, this causes error when using android regex implementation
private val expressionRegex = """(\\*)@\{(([^'}]|('([^'\\]|\\.)*'))*)\}""".toFastRegex()
private val fullMatchExpressionRegex = """^@\{(([^'}]|('([^'\\]|\\.)*'))*)\}$""".toFastRegex()

/**
 * Parser for Server driven expressions.
 *
 * A string must be converted to an expression if it contains the pattern `@{.*}`.
 *
 * Considering the string contains the aforementioned pattern, any `@{` can be escaped with `\`. When an expression
 * is escaped, the parser will return the StringTemplate with the Literal corresponding to the escaped string. Example:
 * `\@{myState}` will be parsed as `StringTemplate(listOf(Literal("@{myState}")))`.
 */
class ExpressionParser(scope: NimbusScope) {
  private val stateReferenceParser = StateReferenceParser(scope)
  private val operationParser = OperationParser(scope)
  private val stringTemplateParser = StringTemplateParser(scope)

  fun parseExpression(code: String, origin: Stateful): Expression {
    // if it's a Literal
    val literal = LiteralParser.parse(code)
    if (literal != null) return literal

    // if it's an Operation
    val isOperation = code.contains("(")
    if (isOperation) return operationParser.parse(code, origin)

    // otherwise, it's a state reference
    return stateReferenceParser.parse(code, origin)
  }

  fun containsExpression(string: String): Boolean {
    return expressionRegex.containsMatchIn(string)
  }

  fun parseString(stringContainingExpression: String, origin: Stateful): Expression {
    val fullMatch = fullMatchExpressionRegex.findWithGroups(stringContainingExpression)
    if (fullMatch != null) {
      val (code) = fullMatch.destructured
      return parseExpression(code, origin)
    }
    return stringTemplateParser.parse(stringContainingExpression, origin)
  }
}
