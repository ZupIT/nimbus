package com.zup.nimbus.core

import platform.Foundation.NSMakeRange
import platform.Foundation.NSRegularExpression
import platform.Foundation.NSString
import platform.Foundation.NSTextCheckingResult
import platform.Foundation.firstMatchInString
import platform.Foundation.matchesInString
import platform.Foundation.numberOfRanges
import platform.Foundation.rangeAtIndex
import platform.Foundation.substringWithRange

actual class FastRegex actual constructor(pattern: String) {
  private val regex = NSRegularExpression(pattern, 0, null)

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

  actual fun find(input: String): String? {
    val match = findMatch(input) ?: return null
    return nsStr(input).substringWithRange(match.range())
  }

  actual fun findWithGroups(input: String): List<String>? {
    val match = findMatch(input) ?: return null
    val result = mutableListOf<String>()
    for (i in 0 until match.numberOfRanges().toInt()) {
      result.add(nsStr(input).substringWithRange(match.rangeAtIndex(i.toULong())))
    }
    return result
  }

  actual fun findAll(input: String): List<String> {
    val matches = findAllMatches(input)
    val ns = nsStr(input)
    return matches.map { ns.substringWithRange(it.range()) }
  }

  actual fun findAllWithGroups(input: String): List<List<String>> {
    val matches = findAllMatches(input)
    val ns = nsStr(input)
    return matches.map {
      val result = mutableListOf<String>()
      for (i in 0 until it.numberOfRanges().toInt()) {
        result.add(ns.substringWithRange(it.rangeAtIndex(i.toULong())))
      }
      result
    }
  }

  actual fun matches(input: String): Boolean {
    val match = findMatch(input)
    return match != null && nsStr(input).substringWithRange(match.range) == input
  }
}

