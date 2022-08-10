package com.zup.nimbus.core

expect class FastRegex(pattern: String) {
  /**
   * Returns the first matched string of a regular expression in the input.
   *
   * @param input the string to match the regex against.
   * @return the first matched string.
   */
  fun find(input: String): String?

  /**
   * Returns the groups of the first match of a regular expression in the input.
   *
   * @param input the string to match the regex against.
   * @return the first matched string.
   */
  fun findWithGroups(input: String): List<String>?

  /**
   * Returns all matched strings of a regular expression in the input.
   *
   * @param input the string to match the regex against.
   * @return a list with all matched strings.
   */
  fun findAll(input: String): List<String>

  /**
   * Returns the groups of all matches of a regular expression in the input.
   *
   * @param input the string to match the regex against.
   * @return a matrix where each line is a match and each column is a matched group. e.g. `result[0][1]` is the second
   * group of the first match.
   */
  fun findAllWithGroups(input: String): List<List<String>>

  /**
   * Indicates whether the regular expression matches the entire input.
   *
   * @param input the string to match the regex against.
   * @return true if the entire input matches the regex; false otherwise.
   */
  fun matches(input: String): Boolean
}
