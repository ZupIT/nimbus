package com.zup.nimbus.core.regex

expect class FastRegex(pattern: String) {
  val pattern: String

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
   * @return the groups of first match.
   */
  fun findWithGroups(input: String): MatchGroups?

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
   * @return a list of matches with their groups.
   */
  fun findAllWithGroups(input: String): List<MatchGroups>

  /**
   * Indicates whether the regular expression matches the entire input.
   *
   * @param input the string to match the regex against.
   * @return true if the entire input matches the regex; false otherwise.
   */
  fun matches(input: String): Boolean

  /**
   * Indicates whether the regular expression can find at least one match in the specified input.
   *
   * @param input the string to match the regex against.
   * @return true if the entire input matches the regex; false otherwise.
   */
  fun containsMatchIn(input: String): Boolean

  /**
   * Replaces all occurrences of this regular expression in the specified input string with specified replacement
   * expression.
   *
   * @param input the string to match the regex against.
   * @param replacement the string to replace the occurrences of the regex in the input.
   * @return the new string with the replaced values.
   */
  fun replace(input: String, replacement: String): String

  /**
   * Replaces all occurrences of this regular expression in the specified input string with the string returned by the
   * transform function.
   *
   * @param input the string to match the regex against.
   * @param transform a function to transform the matched string into a new string. Receives the MatchGroups of the
   * current occurrence and must return the replacement String.
   * @return the new string with the replaced values.
   */
  fun replace(input: String, transform: (MatchGroups) -> String): String
}
