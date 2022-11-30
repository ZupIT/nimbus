package br.com.zup.nimbus.core.expression.parser

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.expression.Expression
import br.com.zup.nimbus.core.regex.toFastRegex

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
class ExpressionParser(nimbus: Nimbus) {
  private val stateReferenceParser = StateReferenceParser(nimbus)
  private val operationParser = OperationParser(nimbus)
  private val stringTemplateParser = StringTemplateParser(nimbus)

  fun parseExpression(code: String): Expression {
    // if it's a Literal
    val literal = LiteralParser.parse(code)
    if (literal != null) return literal

    // if it's an Operation
    val isOperation = code.contains("(")
    if (isOperation) return operationParser.parse(code)

    // otherwise, it's a state reference
    return stateReferenceParser.parse(code)
  }

  fun containsExpression(string: String): Boolean {
    return expressionRegex.containsMatchIn(string)
  }

  fun parseString(stringContainingExpression: String): Expression {
    val fullMatch = fullMatchExpressionRegex.findWithGroups(stringContainingExpression)
    if (fullMatch != null) {
      val (code) = fullMatch.destructured
      return parseExpression(code)
    }
    return stringTemplateParser.parse(stringContainingExpression)
  }
}
