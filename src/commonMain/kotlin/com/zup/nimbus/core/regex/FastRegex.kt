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

  /**
   * Temporary solution for parsing expressions. We'll probably need to reformulate this when revising the
   * implementation and extending the grammar.
   *
   * This replaces every unmatching substring with the transformUnmatching function passed as parameter and every
   * matching substring with the transformMatching substring passed as parameter. The result is a list of whatever
   * the substrings have been transformed into.
   *
   * @param input the string to match the regex against.
   * @param transformUnmatching a function to transform the unmatched substring into T.
   * @param transformMatching a function to transform the matched substring into T.
   * @return the list of T with the replaced values.
   */
  fun <T>transform(input: String, transformUnmatching: (String) -> T, transformMatching: (MatchGroups) -> T): List<T>
}
