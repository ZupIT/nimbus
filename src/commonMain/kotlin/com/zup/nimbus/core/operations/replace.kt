package com.zup.nimbus.core.operations

import com.zup.nimbus.core.OperationHandler

private fun replaceString(text: String, oldTerm: String, newTerm: String): String {
  return text.replace(oldTerm, newTerm)
}

private fun replaceRegex(text: String, oldTerm: Regex, newTerm: String): String {
  return text.replace(oldTerm, newTerm)
}

val replace: OperationHandler = {
  if (it[1] is String) {
    replaceString(it[0] as String, it[1] as String, it[2] as String)
  }
  else if (it[1] is Regex) {
    replaceRegex(it[0] as String, it[1] as Regex, it[2] as String)
  }
  else {
    it[0] as String
  }
}
