package com.zup.nimbus.core.ui.operations

import com.zup.nimbus.core.ui.UILibrary
import com.zup.nimbus.core.regex.replace
import com.zup.nimbus.core.regex.matches
import com.zup.nimbus.core.regex.toFastRegex

private fun toStringList(values: List<Any?>): List<String> {
  return values.filterIsInstance<String>()
}

internal fun registerStringOperations(library: UILibrary) {
  library
    .addOperation("capitalize"){
      (it[0] as String).replaceFirstChar { char -> char.uppercaseChar() }
    }
    .addOperation("lowercase"){
      (it[0] as String).lowercase()
    }
    .addOperation("uppercase"){
      (it[0] as String).uppercase()
    }
    .addOperation("match"){
      val (value, regex) = toStringList(it)
      value.matches(regex.toFastRegex())
    }
    .addOperation("replace"){
      val (value, regex, replace) = toStringList(it)
      value.replace(regex.toFastRegex(), replace)
    }
    .addOperation("substr"){
      val value = it[0] as String
      val start = it[1] as Int
      val end = it.getOrNull(2) as Int?
      if (end == null) value.substring(start) else value.substring(start, end)
    }
}
