package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun toStringList(values: Array<Any?>): List<String> {
  return values.toList().filterIsInstance<String>()
}

internal fun getStringOperations(): Map<String, OperationHandler> {
  return mapOf(
    "capitalize" to {
      (it[0] as String).replaceFirstChar { char -> char.uppercaseChar() }
    },
    "lowercase" to {
      (it[0] as String).lowercase()
    },
    "uppercase" to {
      (it[0] as String).uppercase()
    },
    "match" to {
      val (value, regex) = toStringList(it)
      value.matches(regex.toRegex())
    },
    "replace" to {
      val (value, regex, replace) = toStringList(it)
      value.replace(regex.toRegex(), replace)
    },
    "substr" to {
      val value = it[0] as String
      val start = it[1] as Int
      val end = it.getOrNull(2) as Int?
      if (end == null) value.substring(start) else value.substring(start, end)
    }
  )
}
