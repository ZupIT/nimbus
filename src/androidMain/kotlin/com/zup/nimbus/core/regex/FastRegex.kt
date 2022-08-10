package com.zup.nimbus.core.regex

actual class FastRegex actual constructor(pattern: String) {
  private val regex = pattern.toRegex()

  actual fun find(input: String): String? {
    return regex.find(input)?.value
  }

  actual fun findWithGroups(input: String): MatchGroups? {
    val values = regex.find(input)?.groupValues ?: return null
    return MatchGroups(values)
  }

  actual fun findAll(input: String): List<String> {
    val matched = regex.findAll(input)
    return matched.map { it.value }.toList()
  }

  actual fun findAllWithGroups(input: String): List<MatchGroups> {
    val matched = regex.findAll(input)
    return matched.map { MatchGroups(it.groupValues) }.toList()
  }

  actual fun matches(input: String): Boolean {
    return regex.matches(input)
  }

  actual fun containsMatchIn(input: String): Boolean {
    return regex.containsMatchIn(input)
  }

  actual fun replace(input: String, replacement: String): String {
    return regex.replace(input, replacement)
  }
}
