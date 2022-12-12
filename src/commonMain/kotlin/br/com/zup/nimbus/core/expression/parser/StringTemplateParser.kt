/*
 * Copyright 2023 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.nimbus.core.expression.parser

import br.com.zup.nimbus.core.Nimbus
import br.com.zup.nimbus.core.expression.Literal
import br.com.zup.nimbus.core.expression.StringTemplate
import br.com.zup.nimbus.core.regex.toFastRegex

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
