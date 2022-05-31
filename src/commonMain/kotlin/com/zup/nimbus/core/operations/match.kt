package com.zup.nimbus.core.operations

fun match(text: String, matcher: Regex): Boolean {
  return text.matches(matcher)
}
