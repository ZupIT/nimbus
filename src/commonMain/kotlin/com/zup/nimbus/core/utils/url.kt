package com.zup.nimbus.core.utils

class UriUtils {
  companion object Factory {
    private val charMap: Map<String, String> = mapOf(
      "!" to "%21", " " to "%20", "#" to "%23", "$" to "%24", "&" to "%26", "'" to "%27", "(" to "%28",
      ")" to "%29", "*" to "%2A", "+" to "%2B", "," to "%2C", ":" to "%3A", ";" to "%3B", "=" to "%3D",
      "?" to "%3F", "@" to "%40", "[" to "%5B", "]" to "%5D", "%" to "%25",
    )

    private fun replaceUri(uri: String, isDecode: Boolean = false): String {
      var encoded = uri
      charMap.entries.forEach {
        encoded = encoded.replace(isDecode then it.value ?: it.key, isDecode then it.key ?: it.value)
      }
      return encoded
    }

    fun encode(uri: String): String = replaceUri(uri)

    fun decode(uri: String): String = replaceUri(uri, true)
  }
}
