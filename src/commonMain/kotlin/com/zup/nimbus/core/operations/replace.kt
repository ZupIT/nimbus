package com.zup.nimbus.core.operations

fun replace(text: String, oldTerm: String, newTerm: String): String {
  return text.replace(oldTerm, newTerm)
}

fun replace(text: String, oldTerm: Regex, newTerm: String): String {
  return text.replace(oldTerm, newTerm)
}
