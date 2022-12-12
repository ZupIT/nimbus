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

import br.com.zup.nimbus.core.expression.Literal
import br.com.zup.nimbus.core.regex.matches
import br.com.zup.nimbus.core.regex.toFastRegex

private val literalRegex = """^\d+((.)|(.\d+)?)$""".toFastRegex()

object LiteralParser {
  fun parse(code: String): Literal? {
    when (code) {
      "true" -> return Literal(true)
      "false" -> return Literal(false)
      "null" -> return Literal(null)
    }

    if (code.matches(literalRegex)) {
      if (code.contains(".")) return Literal(code.toDouble())
      return Literal(code.toInt())
    }

    if (code.startsWith("'") && code.endsWith("'")) {
      return Literal(code.drop(1).dropLast(1).replace("""\'""", "'"))
    }

    return null
  }
}
