package com.zup.nimbus.core.utils

import com.zup.nimbus.core.regex.FastRegex

/**
 * Removes a prefix from the string.
 *
 * @param str the string to have the `prefix` removed from
 * @param prefix the prefix to remove from the string
 * @returns the string without the prefix
 */
fun removePrefix(str: String, prefix: String): String {
  return if (str.startsWith(prefix)) str.drop(prefix.length) else str
}

/**
 * Adds a single character to start of the string if the string doesn't yet start with this
 * character.
 *
 * @param str the string to add the prefix character to
 * @param prefix the single character to add as prefix
 * @returns the resulting string
 */
fun addPrefix(str: String, prefix: String): String {
  return ((str.isEmpty() || str[0].toString() != prefix) then "${prefix}${str}") ?: str
}

/**
 * Removes a suffix from the string.
 *
 * @param str the string to have the `suffix` removed from
 * @param suffix the suffix to remove from the string
 * @returns the string without the suffix
 */
fun removeSuffix(str: String, suffix: String): String {
  return if (str.endsWith(suffix)) str.dropLast(suffix.length) else str
}

/**
 * Creates a FastRegex from this string.
 */
fun String.toFastRegex() = FastRegex(this)

