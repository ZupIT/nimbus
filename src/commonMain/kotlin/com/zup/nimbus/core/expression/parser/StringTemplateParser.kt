package com.zup.nimbus.core.expression.parser

import com.zup.nimbus.core.Nimbus
import com.zup.nimbus.core.expression.Literal
import com.zup.nimbus.core.expression.StringTemplate
import com.zup.nimbus.core.regex.toFastRegex

private val expressionRegex = """(\\*)@\{(([^'}]|('([^'\\]|\\.)*'))*)\}""".toFastRegex()

class StringTemplateParser(private val nimbus: Nimbus) {
  fun parse(stringContainingExpression: String): StringTemplate {
    val composition = expressionRegex.transform(stringContainingExpression, { Literal(it) }) {
      val (slashes, code) = it.destructured
      val isExpressionEscaped = slashes.length % 2 == 1
      val escapedSlashes = slashes.replace("""\\""", """\""")

      if (isExpressionEscaped) return@transform Literal("${escapedSlashes.dropLast(1)}@{$code}")

      val expression = nimbus.expressionParser.parseExpression(code)
      return@transform (
        if (escapedSlashes.isEmpty()) expression
        else StringTemplate(listOf(Literal(escapedSlashes), expression))
      )
    }
    return StringTemplate(composition)
  }
}
