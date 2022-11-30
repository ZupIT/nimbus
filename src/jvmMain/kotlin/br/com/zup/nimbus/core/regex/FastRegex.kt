package br.com.zup.nimbus.core.regex

actual class FastRegex actual constructor(actual val pattern: String) {
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

  actual fun replace(input: String, transform: (MatchGroups) -> String): String {
    return regex.replace(input) {
      transform(MatchGroups(it.groupValues))
    }
  }

  actual fun <T>transform(
    input: String,
    transformUnmatching: (String) -> T,
    transformMatching: (MatchGroups) -> T,
  ): List<T> {
    val matches = regex.findAll(input)
    val parts = mutableListOf<T>()
    var next = 0
    matches.forEach {
      parts.add(transformUnmatching(input.substring(next, it.range.first)))
      parts.add(transformMatching(MatchGroups(it.groupValues)))
      next = it.range.last + 1
    }
    parts.add(transformUnmatching(input.substring(next)))
    return parts
  }
}
