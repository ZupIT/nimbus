package com.zup.nimbus.core.unit

/**
 * Removes a prefix from the string.
 *
 * @param str the string to have the `prefix` removed from
 * @param prefix the prefix to remove from the string
 * @returns the string without the prefix
 */
fun removePrefix(str: String, prefix: String): String {
  return str.replace(Regex("^${prefix}"), "")
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
  return (str.isEmpty() || str[0].toString() != prefix) then "${prefix}${str}" ?: str
}

/**
 * Removes a suffix from the string.
 *
 * @param str the string to have the `suffix` removed from
 * @param suffix the suffix to remove from the string
 * @returns the string without the suffix
 */
fun removeSuffix(str: String, suffix: String): String {
  return str.replace(Regex("${suffix}$"), "")
}

/**
 * Transforms the first letter of the string into an uppercase letter.
 *
 * @param str the string to capitalize
 * @returns the resulting string
 */
fun capitalizeFirstLetter(str: String): String {
  return  (str.isEmpty()) then str ?: "${str[0].toString().uppercase()}${str.slice((1 until (str.length - 1)))}"
}
