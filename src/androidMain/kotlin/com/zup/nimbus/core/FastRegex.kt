package com.zup.nimbus.core

actual class FastRegex actual constructor(pattern: String) {
  private val regex = pattern.toRegex()

  actual fun find(input: String): String? {
    return regex.find(input)?.value
  }

  actual fun findWithGroups(input: String): List<String>? {
    return regex.find(input)?.groupValues
  }

  actual fun findAll(input: String): List<String> {
    val matched = regex.findAll(input)
    return matched.map { it.value }.toList()
  }

  actual fun findAllWithGroups(input: String): List<List<String>> {
    val matched = regex.findAll(input)
    return matched.map { it.groupValues }.toList()
  }

  actual fun matches(input: String): Boolean {
    return regex.matches(input)
  }
}
