package com.zup.nimbus.core.expression

import com.zup.nimbus.core.regex.toFastRegex
import com.zup.nimbus.core.scope.NimbusScope
import com.zup.nimbus.core.tree.stateful.Stateful

private val expressionRegex = """(\\*)@\{(([^'}]|('([^'\\]|\\.)*'))*)\}""".toFastRegex()

class StringTemplateParser(private val scope: NimbusScope) {
  fun parse(stringContainingExpression: String, origin: Stateful): StringTemplate {
    val composition = expressionRegex.transform(stringContainingExpression, { Literal(it) }) {
      val (slashes, code) = it.destructured
      val isExpressionEscaped = slashes.length % 2 == 1
      val escapedSlashes = slashes.replace("""\\""", """\""")

      if (isExpressionEscaped) return@transform Literal("${escapedSlashes.dropLast(1)}@{$code}")

      val expression = scope.getExpressionParser().parseExpression(code, origin)
      return@transform (
        if (escapedSlashes.isEmpty()) expression
        else StringTemplate(listOf(Literal(escapedSlashes), expression))
      )
    }
    return StringTemplate(composition)
  }
}
