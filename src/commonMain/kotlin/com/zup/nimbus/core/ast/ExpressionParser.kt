package com.zup.nimbus.core.ast

import com.zup.nimbus.core.OperationHandler
import com.zup.nimbus.core.log.Logger
import com.zup.nimbus.core.regex.toFastRegex
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
class ExpressionParser(
  logger: Logger,
  operationHandlers: Map<String, OperationHandler>
) {
  private val stateReferenceParser = StateReferenceParser(logger)
  private val operationParser = OperationParser(logger, operationHandlers) { code, origin ->
    parseExpression(code, origin)
  }
  private val stringTemplateParser = StringTemplateParser { code, origin ->
    parseExpression(code, origin)
  }

  private fun parseExpression(code: String, origin: Stateful): Expression {
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

  fun parse(stringContainingExpression: String, origin: Stateful): Expression {
    val fullMatch = fullMatchExpressionRegex.findWithGroups(stringContainingExpression)
    if (fullMatch != null) {
      val (code) = fullMatch.destructured
      return parseExpression(code, origin)
    }
    return stringTemplateParser.parse(stringContainingExpression, origin)
  }
}
