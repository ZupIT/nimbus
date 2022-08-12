package com.zup.nimbus.core.regex

import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.Foundation.NSMakeRange
import platform.Foundation.NSMaxRange
import platform.Foundation.NSRegularExpression
import platform.Foundation.NSString
import platform.Foundation.NSTextCheckingResult
import platform.Foundation.firstMatchInString
import platform.Foundation.matchesInString
import platform.Foundation.numberOfRanges
import platform.Foundation.rangeAtIndex
import platform.Foundation.stringByReplacingMatchesInString
import platform.Foundation.substringWithRange

actual class FastRegex actual constructor(actual val pattern: String) {
  private lateinit var regex: NSRegularExpression

  init {
    memScoped {
      val error = alloc<ObjCObjectVar<NSError?>>()
      try {
        regex = NSRegularExpression(pattern, 0, error.ptr)
      } catch (e: Throwable) {
        val errorMessage = error.value?.localizedDescription
        throw if (errorMessage != null) IllegalArgumentException(errorMessage) else e
      }
    }
  }

  private fun nsStr(str: String): NSString {
    @Suppress("CAST_NEVER_SUCCEEDS")
    return str as NSString
  }

  private fun findMatch(input: String): NSTextCheckingResult? {
    return regex.firstMatchInString(input, 0, NSMakeRange(0, nsStr(input).length))
  }

  private fun findAllMatches(input: String): List<NSTextCheckingResult> {
    @Suppress("UNCHECKED_CAST")
    return regex.matchesInString(input, 0, NSMakeRange(0, nsStr(input).length))
      as List<NSTextCheckingResult>
  }

  private fun collectGroups(string: NSString, match: NSTextCheckingResult): MatchGroups {
    val groups = mutableListOf<String>()
    for (i in 0 until match.numberOfRanges().toInt()) {
      val range = match.rangeAtIndex(i.toULong())
      val group = if (NSMaxRange(range) > string.length) "" else string.substringWithRange(range)
      groups.add(group)
    }
    return MatchGroups(groups)
  }

  actual fun find(input: String): String? {
    val match = findMatch(input) ?: return null
    return nsStr(input).substringWithRange(match.range())
  }

  actual fun findWithGroups(input: String): MatchGroups? {
    val match = findMatch(input) ?: return null
    return collectGroups(nsStr(input), match)
  }

  actual fun findAll(input: String): List<String> {
    val matches = findAllMatches(input)
    val ns = nsStr(input)
    return matches.map { ns.substringWithRange(it.range()) }
  }

  actual fun findAllWithGroups(input: String): List<MatchGroups> {
    val matches = findAllMatches(input)
    val ns = nsStr(input)
    return matches.map { collectGroups(ns, it) }
  }

  actual fun matches(input: String): Boolean {
    val match = findMatch(input)
    return match != null && nsStr(input).substringWithRange(match.range) == input
  }

  actual fun containsMatchIn(input: String): Boolean {
    return findMatch(input) != null
  }

  actual fun replace(input: String, replacement: String): String {
    return regex.stringByReplacingMatchesInString(input, 0,
        NSMakeRange(0, nsStr(input).length), replacement)
  }

  actual fun replace(input: String, transform: (MatchGroups) -> String): String {
    val matches = findAllMatches(input)
    val parts = mutableListOf<String>()
    var next = 0
    matches.forEach {
      val groups = collectGroups(nsStr(input), it)
      val length = groups.values.first().length
      val end = NSMaxRange(it.rangeAtIndex(0)).toInt()
      val start = end - length
      parts.add(input.substring(next, start))
      parts.add(transform(groups))
      next = end
    }
    parts.add(input.substring(next))
    return parts.joinToString("")
  }
}
