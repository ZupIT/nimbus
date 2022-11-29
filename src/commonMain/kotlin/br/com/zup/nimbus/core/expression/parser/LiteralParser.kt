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
